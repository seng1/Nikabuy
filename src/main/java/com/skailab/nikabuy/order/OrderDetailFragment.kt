package com.skailab.nikabuy.order


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.skailab.nikabuy.R
import com.skailab.nikabuy.adapter.OrderItemDetailAdapter
import com.skailab.nikabuy.bottomfragment.BottomImageFragment
import com.skailab.nikabuy.databinding.FragmentOrderDetailBinding
import com.skailab.nikabuy.factory.OrderDetailViewModelFactory
import com.skailab.nikabuy.room.UserDatabase
import com.skailab.nikabuy.viewModels.OrderDetailViewModel
import kotlin.requireNotNull as requireNotNull1


/**
 * A simple [Fragment] subclass.
 */
class OrderDetailFragment : Fragment() {
    private var  _container:ViewGroup?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        var binding = FragmentOrderDetailBinding.inflate(inflater)
        binding.lifecycleOwner = this
        _container = container
        val application = requireNotNull1(value = this.activity).application
        val viewModelFactory = OrderDetailViewModelFactory(UserDatabase.getInstance(application).userDao)
        binding.viewModel = ViewModelProvider(this,viewModelFactory).get(OrderDetailViewModel::class.java)
        binding.viewModel!!.orderId=OrderDetailFragmentArgs.fromBundle(requireArguments()).orderId
        binding.viewModel!!.isUserRequested.observe(viewLifecycleOwner,androidx.lifecycle.Observer { isUserRequest:Boolean? ->
            if(isUserRequest!!){
                if(binding.viewModel!!.userEntity.value==null){
                    container!!.findNavController().navigate(R.id.accountFragment)
                }
                else{
                    binding.viewModel!!.getOrder(requireContext())
                }
            }
        })
        binding.rcvProductItems.adapter = OrderItemDetailAdapter(OrderItemDetailAdapter.OnClickListener {
            var message=it.title
            if(!it.skuText.isNullOrEmpty()){
                message+=System.getProperty("line.separator")
                message+=it.skuText
            }
            val bottomSheet = BottomImageFragment(it.imageUrl!!,message!!)
            bottomSheet.show(requireActivity().supportFragmentManager, "image")
        })
        binding.btnPay.setOnClickListener {
            val bottomSheet = OrderDetailPayFragment(binding.viewModel!!.order.value!!,binding)
            bottomSheet.show(requireActivity().supportFragmentManager, "pay")
        }
        // Inflate the layout for this fragment
        return  binding.root
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_menu, menu)
    }

}
