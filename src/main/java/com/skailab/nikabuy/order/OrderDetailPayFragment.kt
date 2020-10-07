package com.skailab.nikabuy.order


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skailab.nikabuy.R

import com.skailab.nikabuy.databinding.FragmentOrderDetailBinding
import com.skailab.nikabuy.databinding.FragmentOrderDetailPayBinding
import com.skailab.nikabuy.factory.OrderDetailPaymentViewModelFactory
import com.skailab.nikabuy.models.OrderDetail
import com.skailab.nikabuy.room.UserDatabase
import com.skailab.nikabuy.viewModels.OrderDetailPaymentViewModel

/**
 * A simple [Fragment] subclass.
 */
class OrderDetailPayFragment(val order: OrderDetail,val orderbinding: FragmentOrderDetailBinding) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentOrderDetailPayBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val application = requireNotNull(value = this.activity).application
        val viewModelFactory = OrderDetailPaymentViewModelFactory(UserDatabase.getInstance(application).userDao)
        binding.viewModel = ViewModelProvider(this,viewModelFactory).get(OrderDetailPaymentViewModel::class.java)
        binding.viewModel!!.setOrder(order)
        binding.btnPay.setOnClickListener {
            onPayClick(binding)
        }
        // Inflate the layout for this fragment
        return binding.root
    }
    fun onPayClick(binding: FragmentOrderDetailPayBinding){
        if(binding.txtPaymentPassword.text.isNullOrEmpty()){
            binding.viewModel!!.showMadal(requireContext(),getString(R.string.payment_password_require))
            return
        }
        if(binding.txtPaymentPassword.text.toString().length!=4){
            binding.viewModel!!.showMadal(requireContext(),getString(R.string.payment_password_must_4_digit))
            return
        }
        binding.viewModel!!.pay(requireContext(),binding.txtPaymentPassword.text.toString(),this,orderbinding)
    }
}
