package com.skailab.nikabuy.order


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skailab.nikabuy.R
import com.skailab.nikabuy.databinding.FragmentOrderPayAllBinding
import com.skailab.nikabuy.factory.OrderListPaymentViewModelFactory
import com.skailab.nikabuy.models.Order
import com.skailab.nikabuy.room.UserDatabase
import com.skailab.nikabuy.viewModels.OrderPayAllViewModel
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter

/**
 * A simple [Fragment] subclass.
 */
class OrderPayAllFragment(val orders:MutableList<Order>, val adapter: SectionedRecyclerViewAdapter) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentOrderPayAllBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val application = requireNotNull(value = this.activity).application
        val viewModelFactory = OrderListPaymentViewModelFactory(UserDatabase.getInstance(application).userDao)
        binding.viewModel = ViewModelProvider(this,viewModelFactory).get(OrderPayAllViewModel::class.java)
        binding.viewModel!!.setOrder(orders)
        binding.btnPay.setOnClickListener {
            onPayClick(binding,adapter)
        }
        // Inflate the layout for this fragment
        return  binding.root
    }
    fun onPayClick(binding: FragmentOrderPayAllBinding, adapter: SectionedRecyclerViewAdapter){
        if(binding.txtPaymentPassword.text.isNullOrEmpty()){
            binding.viewModel!!.showMadal(requireContext(),getString(R.string.payment_password_require))
            return
        }
        if(binding.txtPaymentPassword.text.toString().length!=4){
            binding.viewModel!!.showMadal(requireContext(),getString(R.string.payment_password_must_4_digit))
            return
        }
        binding.viewModel!!.pay(binding.txtPaymentPassword.text.toString(),adapter,requireActivity(),requireContext(),this)
    }

}
