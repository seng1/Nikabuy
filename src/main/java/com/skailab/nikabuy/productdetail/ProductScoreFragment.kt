package com.skailab.nikabuy.productdetail


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skailab.nikabuy.EndlessRecyclerViewScrollListener
import com.skailab.nikabuy.adapter.ProductAdapter
import com.skailab.nikabuy.audio.AudioFragmentDirections
import com.skailab.nikabuy.databinding.FragmentProductScoreBinding
import com.skailab.nikabuy.factory.ProdutScoreViewModelFactory
import com.skailab.nikabuy.models.ProductDetail
import com.skailab.nikabuy.room.UserDatabase
import com.skailab.nikabuy.room.productcache.ProductDatabase
import com.skailab.nikabuy.viewModels.ProductScoreViewModel
import kotlin.requireNotNull as requireNotNull1


/**
 * A simple [Fragment] subclass.
 */
class ProductScoreFragment : Fragment() {
    private  var adapter: ProductAdapter?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentProductScoreBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val application = requireNotNull1(value = this.activity).application
        val viewModelFactory = ProdutScoreViewModelFactory(UserDatabase.getInstance(application).userDao,
            ProductDatabase.getInstance(application).productDao)
        binding.viewModel = ViewModelProvider(this,viewModelFactory).get(ProductScoreViewModel::class.java)
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
        if(requireArguments()!=null){
            binding.viewModel!!.setProductFromAguement(ProductDetailFragmentArgs.fromBundle(requireArguments()).productDetail)
        }

        binding.viewModel!!.isUserRequested.observe(viewLifecycleOwner,androidx.lifecycle.Observer { isUserRequest:Boolean? ->
            if(isUserRequest!!){

                if(binding.viewModel!!.userEntity.value!=null && binding.viewModel!!.product!=null){
                    binding.viewModel!!.getInitProducts(requireContext(),adapter!!)
                }
            }
        })
        binding.rcvProductItems.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    binding.viewModel!!.setShowDetail(false)
                } else {
                    binding.viewModel!!.setShowDetail(true)
                }
            }
        })
        binding.viewModel!!.productDetail.observe(binding.lifecycleOwner!!, Observer {
                pr: ProductDetail ->
            if(pr!=null &&  !pr.isAlreadyDisplay!!){
                pr.isAlreadyDisplay=true
                container!!.findNavController().navigate(
                   ProductScoreFragmentDirections.actionProductScoreFragmentToProductDetailFragment(pr))
            }
        })
        // Inflate the layout for this fragment
        return  binding.root
    }


}
