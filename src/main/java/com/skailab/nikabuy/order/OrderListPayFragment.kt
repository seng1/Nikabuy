package com.skailab.nikabuy.order


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skailab.nikabuy.R

import com.skailab.nikabuy.databinding.FragmentOrderListPayBinding
import com.skailab.nikabuy.factory.OrderListPaymentViewModelFactory
import com.skailab.nikabuy.models.OrderForBindingList
import com.skailab.nikabuy.room.UserDatabase
import com.skailab.nikabuy.viewModels.OrderListPaymentViewModel

/**
 * A simple [Fragment] subclass.
 */
class OrderListPayFragment(val order:OrderForBindingList) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val binding = FragmentOrderListPayBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val application = requireNotNull(value = this.activity).application
        val viewModelFactory = OrderListPaymentViewModelFactory(UserDatabase.getInstance(application).userDao)
        binding.viewModel = ViewModelProvider(this,viewModelFactory).get(OrderListPaymentViewModel::class.java)
        binding.viewModel!!.setOrder(order)
        binding.btnPay.setOnClickListener {
            onPayClick(binding)
        }
        // Inflate the layout for this fragment
        return  binding.root
    }
    fun onPayClick(binding: FragmentOrderListPayBinding){
        if(binding.txtPaymentPassword.text.isNullOrEmpty()){
            binding.viewModel!!.showMadal(requireContext(),getString(R.string.payment_password_require))
            return
        }
        if(binding.txtPaymentPassword.text.toString().length!=4){
            binding.viewModel!!.showMadal(requireContext(),getString(R.string.payment_password_must_4_digit))
            return
        }
       binding.viewModel!!.pay(requireContext(),binding.txtPaymentPassword.text.toString(),this)
    }
}
