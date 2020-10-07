package com.skailab.nikabuy.payment


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.skailab.nikabuy.R

import com.skailab.nikabuy.databinding.FragmentPaymentPasswordBinding
import com.skailab.nikabuy.factory.PaymentPasswordViewModelFactory
import com.skailab.nikabuy.room.UserDatabase
import com.skailab.nikabuy.viewModels.PaymentPasswordViewModel

/**
 * A simple [Fragment] subclass.
 */
class PaymentPasswordFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val binding = FragmentPaymentPasswordBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val application = requireNotNull(value = this.activity).application
        val viewModelFactory = PaymentPasswordViewModelFactory(UserDatabase.getInstance(application).userDao)
        binding.viewModel = ViewModelProvider(this,viewModelFactory).get(PaymentPasswordViewModel::class.java)
        binding.btnSave.setOnClickListener {
            onSubmit(binding,container!!)
        }
        // Inflate the layout for this fragment
        return binding.root
    }
    fun onSubmit(binding: FragmentPaymentPasswordBinding,container: ViewGroup){
        if(binding.txtPaymentPassword.text.isNullOrEmpty()){
            binding.viewModel!!.showMadal(requireContext(),getString(R.string.payment_password_require))
            return
        }
        val totalLength=binding.txtPaymentPassword.text.toString().length
        if(totalLength!=4){
            binding.viewModel!!.showMadal(requireContext(),getString(R.string.payment_password_must_4_digit))
            return
        }
        binding.viewModel!!.hideKeyboard(requireContext(),requireView())
        binding.viewModel!!.Save(requireContext(),container,binding.txtPaymentPassword.text.toString())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_menu, menu)
    }
}
