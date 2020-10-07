package com.skailab.nikabuy.audio


import android.Manifest.*
import android.os.Bundle
import android.os.Environment
import android.view.*
import android.widget.AdapterView.OnItemClickListener
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skailab.nikabuy.EndlessRecyclerViewScrollListener
import com.skailab.nikabuy.R
import com.skailab.nikabuy.Space
import com.skailab.nikabuy.WavRecorder
import com.skailab.nikabuy.adapter.ProductAdapter
import com.skailab.nikabuy.databinding.FragmentAudioBinding
import com.skailab.nikabuy.factory.AudioViewModelFactory
import com.skailab.nikabuy.models.ProductDetail
import com.skailab.nikabuy.room.UserDatabase
import com.skailab.nikabuy.room.productcache.ProductDatabase
import com.skailab.nikabuy.viewModels.AudioViewModel
import kotlin.arrayOf as arrayOf1

/**
 * A simple [Fragment] subclass.
 */
class AudioFragment : Fragment() {
    private  var adapter:ProductAdapter?=null
    private var output: String? = null
    private  var wavRecorder: WavRecorder?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        val binding = FragmentAudioBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val application = requireNotNull(this.activity).application
        val viewModelFactory = AudioViewModelFactory(UserDatabase.getInstance(application).userDao,
            ProductDatabase.getInstance(application).productDao)
        binding.viewModel = ViewModelProvider(this,viewModelFactory).get(AudioViewModel::class.java)
        adapter= ProductAdapter(ProductAdapter.OnClickListener {
            binding.viewModel!!.getProductDetail(requireContext(),it.itemId)
        })
        binding.rcvProductItems.adapter =adapter
        val  scrollListener: EndlessRecyclerViewScrollListener = object : EndlessRecyclerViewScrollListener(binding.rcvProductItems.layoutManager as GridLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                binding.viewModel!!.onGetProducts(context!!,adapter!!)
            }
        }
        binding.rcvProductItems.addOnScrollListener(scrollListener)
        binding.rcvProductItems.addItemDecoration(Space(2, 5, true, 0))
        val gridLayoutManager=binding.rcvProductItems.layoutManager as GridLayoutManager
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (adapter!!.getItemViewType(position)) {
                    adapter!!.PRODUCT_ITEM -> 1
                    adapter!!.LOADING_ITEM -> 2 //number of columns of the grid
                    else -> -1
                }
            }
        }
        binding.viewModel!!.isUserRequested.observe(viewLifecycleOwner,androidx.lifecycle.Observer { isUserRequest:Boolean? ->
            if(isUserRequest!!){
                if(binding.viewModel!!.userEntity.value==null){
                    container!!.findNavController().navigate(R.id.accountFragment)
                }
                else
                {
                    if(binding.viewModel!!.isTaobao(binding.viewModel!!.productProvider)){
                        binding.chnGroupProvider.check(binding.chpTaobao.id)
                    }
                    else{
                        binding.chnGroupProvider.check(binding.chp1688.id)
                    }
                    binding.viewModel!!.filter.provider=binding.viewModel!!.productProvider
                    binding.viewModel!!.filter.language=binding.viewModel!!.getDefaultLanguage()
                    binding.viewModel!!.filter.audioLanguage=binding.viewModel!!.audioSearchLanguage
                    if(binding.viewModel!!.audioSearchLanguage!!.contains("km")){
                        binding.chnGroupLanguage.check(binding.chpKhmer.id)
                    }
                    else if(binding.viewModel!!.audioSearchLanguage!!.contains("zh")){
                        binding.chnGroupLanguage.check(binding.chpChina.id)
                    }
                    else{
                        binding.chnGroupLanguage.check(binding.chpEnlgish.id)
                    }
                }
            }
        })
        binding.chpEnlgish.setOnClickListener {
            binding.viewModel!!.audioSearchLanguage="en"
            binding.viewModel!!.filter.audioLanguage="en"
            binding.viewModel!!.updateAudioLanguage()
        }
        binding.chpKhmer.setOnClickListener {
            binding.viewModel!!.audioSearchLanguage="km"
            binding.viewModel!!.filter.audioLanguage="km"
            binding.viewModel!!.updateAudioLanguage()
        }
        binding.chpChina.setOnClickListener {
            binding.viewModel!!.audioSearchLanguage="zh"
            binding.viewModel!!.filter.audioLanguage="zh"
            binding.viewModel!!.updateAudioLanguage()
        }
        binding.chp1688.setOnClickListener{
            binding.viewModel!!.productProvider=binding.viewModel!!.alibaba1688Provider
            binding.viewModel!!.updateProductProvider()
            binding.viewModel!!.filter.provider=binding.viewModel!!.productProvider
            if (!binding.viewModel!!.filter.itemTitle.isEmpty()) {
                binding.viewModel!!.items.value!!.clear()
                adapter!!.notifyDataSetChanged()
                binding.viewModel!!.filter.page = 1
                binding.viewModel!!.onGetProducts(requireContext(), adapter!!)
            }
        }
        binding.chpTaobao.setOnClickListener {
            binding.viewModel!!.productProvider=binding.viewModel!!.taoboaProvider
            binding.viewModel!!.updateProductProvider()
            binding.viewModel!!.filter.provider=binding.viewModel!!.productProvider
            if (!binding.viewModel!!.filter.itemTitle.isEmpty()) {
                binding.viewModel!!.items.value!!.clear()
                adapter!!.notifyDataSetChanged()
                binding.viewModel!!.filter.page = 1
                binding.viewModel!!.onGetProducts(requireContext(), adapter!!)
            }
        }
        binding.buttonStartRecording.setOnClickListener {
            try{

               val t= (ContextCompat.checkSelfPermission(requireContext(), permission.RECORD_AUDIO ))
                if(t==-1)
                {
                    val permissions = arrayOf1(permission.RECORD_AUDIO, permission.WRITE_EXTERNAL_STORAGE, permission.READ_EXTERNAL_STORAGE)
                    ActivityCompat.requestPermissions(requireActivity(), permissions,0)

                    startRecording(binding.viewModel!!)
                }
                else{
                    startRecording(binding.viewModel!!)
                }
            } catch (ex:Exception){
               binding.viewModel!!.showMadal(requireContext(),ex.message!!)
            }
        }
        binding.buttonStopRecording.setOnClickListener {
            try{
                wavRecorder!!.stopRecording()
                val audioString:String =binding.viewModel!!.convertFileToBaseString(requireContext(),output!!,true)
                binding.viewModel!!._showStopRecordButton.value=false
                binding.viewModel!!.SetImageBaseString(audioString,requireContext(),adapter!!)
            }catch (ex:Exception){
                binding.viewModel!!.showMadal(requireContext(),ex.message!!)
            }
        }
        binding.viewModel!!.productDetail.observe(binding.lifecycleOwner!!, Observer {
                pr: ProductDetail ->
            if(!pr.isAlreadyDisplay!!){
                pr.isAlreadyDisplay=true
                container!!.findNavController().navigate(AudioFragmentDirections.actionAudioFragmentToProductDetailFragment(
                    pr
                ))
            }
        })
        // Inflate the layout for this fragment
        return  binding.root
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_menu, menu)
    }

    private fun startRecording(viewModel: AudioViewModel) {
        try {
           viewModel._showStartRecordButton.value=false
            viewModel._showStopRecordButton.value=true
            output =requireContext().getExternalFilesDir(null)!!.absolutePath + "/recording.wav"
            wavRecorder=WavRecorder(output!!,requireContext())
            wavRecorder!!.startRecording()
        }catch (ex:Exception){
            viewModel.displayException(requireContext(),ex)
        }
    }

}
