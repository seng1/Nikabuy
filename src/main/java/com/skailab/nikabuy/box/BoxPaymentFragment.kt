package com.skailab.nikabuy.box


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

import com.skailab.nikabuy.R
import com.skailab.nikabuy.adapter.BoxAdapter
import com.skailab.nikabuy.databinding.FragmentBoxBinding
import com.skailab.nikabuy.databinding.FragmentBoxPaymentBinding
import com.skailab.nikabuy.databinding.FragmentOrderDetailPayBinding
import com.skailab.nikabuy.factory.BoxPaymentViewModelFactory
import com.skailab.nikabuy.factory.BoxViewModelFactory
import com.skailab.nikabuy.models.Box
import com.skailab.nikabuy.room.UserDatabase
import com.skailab.nikabuy.viewModels.BoxPaymentViewModel
import com.skailab.nikabuy.viewModels.BoxViewModel

/**
 * A simple [Fragment] subclass.
 */
class BoxPaymentFragment(val box:Box,val boxItemViewHolder: BoxAdapter.BoxItemViewHolder) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentBoxPaymentBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val application = requireNotNull(value = this.activity).application
        val viewModelFactory = BoxPaymentViewModelFactory(UserDatabase.getInstance(application).userDao)
        binding.viewModel = ViewModelProvider(this,viewModelFactory).get(BoxPaymentViewModel::class.java)
        binding.viewModel!!.setBox(box)
        binding.btnPay.setOnClickListener {
            onPayClick(binding)
        }
        // Inflate the layout for this fragment
        return binding.root
    }
    fun onPayClick(binding: FragmentBoxPaymentBinding){
        if(binding.txtPaymentPassword.text.isNullOrEmpty()){
            binding.viewModel!!.showMadal(requireContext(),getString(R.string.payment_password_require))
            return
        }
        if(binding.txtPaymentPassword.text.toString().length!=4){
            binding.viewModel!!.showMadal(requireContext(),getString(R.string.payment_password_must_4_digit))
            return
        }
        binding.viewModel!!.pay(requireContext(),binding.txtPaymentPassword.text.toString(),this,boxItemViewHolder)
    }

}
