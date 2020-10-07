package com.skailab.nikabuy.text


import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skailab.nikabuy.EndlessRecyclerViewScrollListener
import com.skailab.nikabuy.R
import com.skailab.nikabuy.Space
import com.skailab.nikabuy.adapter.ProductAdapter
import com.skailab.nikabuy.databinding.FragmentTextBinding
import com.skailab.nikabuy.factory.TextViewModelFactory
import com.skailab.nikabuy.home.HomeFragmentDirections
import com.skailab.nikabuy.models.Product1688
import com.skailab.nikabuy.models.ProductDetail
import com.skailab.nikabuy.models.ProductTaobao
import com.skailab.nikabuy.room.UserDatabase
import com.skailab.nikabuy.room.productcache.ProductDatabase
import com.skailab.nikabuy.viewModels.TextViewModel
import kotlin.requireNotNull as requireNotNull1


/**
 * A simple [Fragment] subclass.
 */
class TextFragment : Fragment() {
    private  var adapter:ProductAdapter?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        val binding = FragmentTextBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val application = requireNotNull1(value = this.activity).application
        val viewModelFactory = TextViewModelFactory(UserDatabase.getInstance(application).userDao,ProductDatabase.getInstance(application).productDao)
        binding.viewModel =ViewModelProvider(this,viewModelFactory).get(TextViewModel::class.java)
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
                else
                {
                    if(binding.viewModel!!.isTaobao(binding.viewModel!!.productProvider)){
                        binding.chnGroupProvider.check(binding.chpTaobao.id)
                    }
                    else{
                        binding.chnGroupProvider.check(binding.chp1688.id)
                    }
                    binding.viewModel!!.filter.provider=binding.viewModel!!.productProvider
                }
            }
        })
        binding.textSearch.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(
                v: View?,
                keyCode: Int,
                event: KeyEvent
            ): Boolean {
                if (event.action === KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER
                ) {
                    var textSearch =binding.textSearch.text.toString()
                    if(textSearch.isNullOrEmpty()){
                        binding.viewModel!!.showMadal(requireContext(),getString(R.string.text_search_require))
                    }
                    else{
                        binding.viewModel!!.hideKeyboard(requireContext(),requireView())
                        binding.viewModel!!.setSearchText(textSearch,requireContext(),adapter!!)
                    }
                    return true
                }
                return false
            }
        })
        binding.viewModel!!.product1688.observe(binding.lifecycleOwner!!, Observer {
                pr: Product1688 ->
            if(!pr.isAlreadyDisplay!!){
                container!!.findNavController().navigate(TextFragmentDirections.actionTextFragmentToProduct1688Fragment(pr))
                pr.isAlreadyDisplay=true
            }
        })
        binding.viewModel!!.productTaobao.observe(binding.lifecycleOwner!!, Observer {
                pr: ProductTaobao ->
            if(!pr.isAlreadyDisplay!!){
                container!!.findNavController().navigate(TextFragmentDirections.actionTextFragmentToProductTaobaoFragment(pr))
                pr.isAlreadyDisplay=true
            }
        })
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
        return  binding.root
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.home_menu, menu)
    }
}
