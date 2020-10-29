package com.skailab.nikabuy.image


import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skailab.nikabuy.EndlessRecyclerViewScrollListener
import com.skailab.nikabuy.R
import com.skailab.nikabuy.Space
import com.skailab.nikabuy.Utility
import com.skailab.nikabuy.adapter.ProductAdapter
import com.skailab.nikabuy.databinding.FragmentImageBinding
import com.skailab.nikabuy.factory.ImageViewModelFactory
import com.skailab.nikabuy.models.Product1688
import com.skailab.nikabuy.models.ProductDetail
import com.skailab.nikabuy.models.ProductTaobao
import com.skailab.nikabuy.room.UserDatabase
import com.skailab.nikabuy.room.productcache.ProductDatabase
import com.skailab.nikabuy.text.TextFragmentDirections
import com.skailab.nikabuy.viewModels.ImageViewModel
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class ImageFragment : Fragment() {
    private val REQUEST_CAMERA = 0
    private val SELECT_FILE = 1
    private var userChoosenTask: String? = null
    private  var _container: ViewGroup?=null
    private  var adapter:ProductAdapter?=null
    private  var viewModel:ImageViewModel?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _container=container
        setHasOptionsMenu(true)
        val binding = FragmentImageBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val application = requireNotNull(this.activity).application
        val viewModelFactory = ImageViewModelFactory(UserDatabase.getInstance(application).userDao,
            ProductDatabase.getInstance(application).productDao)
        viewModel = ViewModelProvider(this,viewModelFactory).get(ImageViewModel::class.java)
        binding.viewModel=viewModel
        adapter= ProductAdapter(ProductAdapter.OnClickListener {
           binding.viewModel!!.getProductDetail(requireContext(),it.itemId)
        })
        binding.rcvProductItems.adapter =adapter
        val  scrollListener = object : EndlessRecyclerViewScrollListener(binding.rcvProductItems.layoutManager as GridLayoutManager) {
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
                    adapter!!.LOADING_ITEM -> binding.viewModel!!.getSpaceCount()
                    else -> -1
                }
            }
        }
        gridLayoutManager.spanCount=binding.viewModel!!.getSpaceCount()
        binding.viewModel!!.isUserRequested.observe(viewLifecycleOwner,androidx.lifecycle.Observer { isUserRequest:Boolean? ->
            if(isUserRequest!!){
                if(binding.viewModel!!.userEntity.value==null){
                    container!!.findNavController().navigate(R.id.accountFragment)
                }
                else{
                   /* if(binding.viewModel!!.isTaobao(binding.viewModel!!.productProvider)){
                        binding.chnGroupProvider.check(binding.chpTaobao.id)
                    }
                    else{
                        binding.chnGroupProvider.check(binding.chp1688.id)
                    }*/
                    binding.viewModel!!.filter.provider=binding.viewModel!!.alibaba1688Provider
                }
            }
        })
        binding.chp1688.setOnClickListener{
            binding.viewModel!!.productProvider=binding.viewModel!!.alibaba1688Provider
            binding.viewModel!!.updateProductProvider()
            binding.viewModel!!.filter.provider=binding.viewModel!!.productProvider
            if (!binding.viewModel!!.filter.imageUrl.isEmpty()) {
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
            if (!binding.viewModel!!.filter.imageUrl.isEmpty()) {
                binding.viewModel!!.items.value!!.clear()
                adapter!!.notifyDataSetChanged()
                binding.viewModel!!.filter.page = 1
                binding.viewModel!!.onGetProducts(requireContext(), adapter!!)
            }
        }
        binding.viewModel!!.product1688.observe(binding.lifecycleOwner!!, androidx.lifecycle.Observer {
                pr: Product1688 ->
            if(!pr.isAlreadyDisplay!!){
                container!!.findNavController().navigate(ImageFragmentDirections.actionImageFragmentToProduct1688Fragment(pr))
                pr.isAlreadyDisplay=true
            }
        })
        binding.viewModel!!.productTaobao.observe(binding.lifecycleOwner!!, androidx.lifecycle.Observer {
                pr: ProductTaobao ->
            if(!pr.isAlreadyDisplay!!){
                container!!.findNavController().navigate(ImageFragmentDirections.actionImageFragmentToProductTaobaoFragment(pr))
                pr.isAlreadyDisplay=true
            }
        })
        binding.btnTakePhoto.setOnClickListener {
            cameraIntent()
        }
        binding.btnBrowFromGallery.setOnClickListener {
            galleryIntent()
        }
        // Inflate the layout for this fragment
        return binding.root
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_menu, menu)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data)
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data!!)
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (userChoosenTask == "Take Photo")
                    cameraIntent()
                else if (userChoosenTask == "Choose from Library")
                    galleryIntent()
            }
        }
    }
    private fun onCaptureImageResult(data: Intent) {
        val thumbnail = data.extras!!.get("data") as Bitmap?
        val bytes = ByteArrayOutputStream()
        thumbnail!!.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        GetBase64(thumbnail)
    }
    private fun cameraIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_CAMERA)
    }
    private fun galleryIntent() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT//
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_image)), SELECT_FILE)
    }
    private fun onSelectFromGalleryResult(data: Intent?) {

        var bm: Bitmap?
        if (data != null) {
            try {
                bm =viewModel!!.convertToBitmap(requireContext(),data.data!!)
                GetBase64(bm)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

    }
    private  fun  GetBase64(thumbnail:Bitmap?){
        val bytes = ByteArrayOutputStream()
        thumbnail!!.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        var baseString= Base64.getEncoder().encodeToString(bytes.toByteArray())
        viewModel!!.SetImageBaseString(baseString,requireContext(),adapter!!)
    }
}
