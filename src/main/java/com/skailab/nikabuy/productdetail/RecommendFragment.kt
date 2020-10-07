package com.skailab.nikabuy.productdetail


import android.os.Bundle
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skailab.nikabuy.EndlessRecyclerViewScrollListener

import com.skailab.nikabuy.R
import com.skailab.nikabuy.adapter.ProductAdapter
import com.skailab.nikabuy.databinding.FragmentProductDescriptionBinding
import com.skailab.nikabuy.databinding.FragmentRecommendBinding
import com.skailab.nikabuy.factory.ProductDescriptionViewModelFactory
import com.skailab.nikabuy.factory.RecommendViewModelFactory
import com.skailab.nikabuy.models.ProductDetail
import com.skailab.nikabuy.room.UserDatabase
import com.skailab.nikabuy.room.productcache.ProductDatabase
import com.skailab.nikabuy.viewModels.ProductDescriptionViewModel
import com.skailab.nikabuy.viewModels.RecommendViewModel
import kotlin.requireNotNull as requireNotNull1

/**
 * A simple [Fragment] subclass.
 */
class RecommendFragment : Fragment() {
    private  var adapter: ProductAdapter?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentRecommendBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val application = requireNotNull1<FragmentActivity>(value = this.activity).application
        val viewModelFactory = RecommendViewModelFactory(UserDatabase.getInstance(application).userDao,
            ProductDatabase.getInstance(application).productDao)
        binding.viewModel = ViewModelProvider(this,viewModelFactory).get(RecommendViewModel::class.java)
        if(requireArguments()!=null){
            binding.viewModel!!.setProductFromAgument(RecommendFragmentArgs.fromBundle(requireArguments()).productDetail)
        }
        adapter= ProductAdapter(ProductAdapter.OnClickListener {
            binding.viewModel!!.getProductDetail(requireContext(),it.itemId)
        })
        binding.rcvProductItems.adapter =adapter
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
        val  scrollListener: EndlessRecyclerViewScrollListener = object : EndlessRecyclerViewScrollListener(binding.rcvProductItems.layoutManager as GridLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                binding.viewModel!!.onGetProducts(context!!,adapter!!)
            }
        }
        binding.rcvProductItems.addOnScrollListener(scrollListener)
        binding.viewModel!!.isUserRequested.observe(viewLifecycleOwner,androidx.lifecycle.Observer { isUserRequest:Boolean? ->
            if(isUserRequest!!){
                if(binding.viewModel!!.userEntity.value!=null && binding.viewModel!!.product!=null){
                    binding.viewModel!!.getInitProducts(requireContext(),adapter!!)
                }
            }
        })
        binding.viewModel!!.productDetail.observe(binding.lifecycleOwner!!, Observer {
                pr: ProductDetail ->
            if(pr!=null &&  !pr.isAlreadyDisplay!!){
                pr.isAlreadyDisplay=true
                container!!.findNavController().navigate(
                    RecommendFragmentDirections.actionRecommentFragmentToProductDetailFragment(pr))
            }
        })

        // Inflate the layout for this fragment
       return binding.root
    }


}
