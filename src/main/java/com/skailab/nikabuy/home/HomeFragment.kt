package com.skailab.nikabuy.home


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.skailab.nikabuy.EndlessRecyclerViewScrollListener

import com.skailab.nikabuy.R
import com.skailab.nikabuy.Space
import com.skailab.nikabuy.adapter.ProductAdapter
import com.skailab.nikabuy.databinding.FragmentHomeBinding
import com.skailab.nikabuy.models.Buyer
import com.skailab.nikabuy.models.Product1688
import com.skailab.nikabuy.models.ProductTaobao
import com.skailab.nikabuy.models.SaleMan
import com.skailab.nikabuy.room.UserDatabase
import com.skailab.nikabuy.room.productcache.ProductDatabase
import com.skailab.nikabuy.viewModels.HomeViewModel
/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {
    private  var adapter:ProductAdapter?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val binding = FragmentHomeBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val application = requireNotNull(this.activity).application
        binding.viewModel=  HomeViewModel(UserDatabase.getInstance(application).userDao,
            ProductDatabase.getInstance(application).productDao)
        adapter= ProductAdapter(ProductAdapter.OnClickListener {
            if(binding.viewModel!!.userEntity.value==null){
                container!!.findNavController().navigate(R.id.accountFragment)
            }
            else{
                binding.viewModel!!.getProductDetail(requireContext(),it.itemId)
            }
        })
        binding.rcvProductItems.adapter =adapter
        val  scrollListener = object : EndlessRecyclerViewScrollListener(binding.rcvProductItems.layoutManager as GridLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
               binding.viewModel!!.onGetProducts(context!!,adapter!!)
            }
        }

        binding.rcvProductItems.addOnScrollListener(scrollListener)
        var gridLayoutManager=binding.rcvProductItems.layoutManager as GridLayoutManager
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                when (adapter!!.getItemViewType(position)) {
                    adapter!!.PRODUCT_ITEM -> return 1
                    adapter!!.LOADING_ITEM -> return binding.viewModel!!.getSpaceCount()
                    else -> return -1
                }
            }
        }
        gridLayoutManager.spanCount=binding.viewModel!!.getSpaceCount()
        binding.rcvProductItems.addItemDecoration(Space(binding.viewModel!!.getSpaceCount(), 5, true, 0))

        binding.viewModel!!.isUserRequested.observe(viewLifecycleOwner,androidx.lifecycle.Observer { isUserRequest:Boolean? ->
            if(isUserRequest!!){
                if(binding.viewModel!!.userEntity.value==null){
                    binding.viewModel!!.onInit(requireContext(),adapter!!)
                }
                else{
                    binding.viewModel!!.onInit(requireContext(),adapter!!)
                    binding.viewModel!!.getBuyer(requireContext())
                }
            }
        })
        binding.viewModel!!.buyer.observe(viewLifecycleOwner,androidx.lifecycle.Observer { buyer:Buyer? ->
            if(buyer!=null){
                if(buyer.saleManId==null){
                    container!!.findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToUpdateSaleManFragment())
                }
                else if(buyer.paymentPasswordCreated!=null && buyer.paymentPasswordCreated==false){
                    container!!.findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToNewPaymentPasswordFragment())
                }
            }
        })
        binding.viewModel!!.product1688.observe(binding.lifecycleOwner!!, Observer {
                pr: Product1688 ->
            if(!pr.isAlreadyDisplay!!){
                container!!.findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToProduct1688Fragment(pr))
                pr.isAlreadyDisplay=true
            }
        })
        binding.viewModel!!.productTaobao.observe(binding.lifecycleOwner!!, Observer {
                pr: ProductTaobao ->
            if(!pr.isAlreadyDisplay!!){
                container!!.findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToProductTaobaoFragment(pr))
                pr.isAlreadyDisplay=true
            }

        })
        // Inflate the layout for this fragment
        return  binding.root;
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.home_menu, menu)
    }
}
