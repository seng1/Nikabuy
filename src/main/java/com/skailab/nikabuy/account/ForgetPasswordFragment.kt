package com.skailab.nikabuy.account


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController

import com.skailab.nikabuy.R
import com.skailab.nikabuy.databinding.FragmentForgetPasswordBinding
import com.skailab.nikabuy.databinding.FragmentLoginBinding
import com.skailab.nikabuy.factory.ForgetPasswordViewModelFactory
import com.skailab.nikabuy.factory.LoginViewModelFactory
import com.skailab.nikabuy.room.UserDatabase
import com.skailab.nikabuy.viewModels.ForgetPasswordViewModel
import com.skailab.nikabuy.viewModels.LoginViewModel

/**
 * A simple [Fragment] subclass.
 */
class ForgetPasswordFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentForgetPasswordBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val application = requireNotNull(value = this.activity).application
        val viewModelFactory =
            ForgetPasswordViewModelFactory(UserDatabase.getInstance(application).userDao)
        binding.viewModel =
            ViewModelProvider(this, viewModelFactory).get(ForgetPasswordViewModel::class.java)
        binding.viewModel!!.isUserRequested.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { isUserRequest: Boolean? ->
                if (isUserRequest!!) {
                    if (binding.viewModel!!.userEntity.value != null) {
                        container!!.findNavController().navigate(R.id.homeFragment)
                    }
                }
            })
        binding.btnSendVerification.setOnClickListener {
            onSendVerificationCode(binding)
        }
        binding.btnNotReceiveVerificationCode.setOnClickListener {
            binding.viewModel!!.sendVerificationCode(requireContext())
        }
        binding.btnConfirmVerificationCode.setOnClickListener {
            binding.viewModel!!.confirmVerificationCode(binding.txtVerificationCode.text.toString(),requireContext())
        }
        binding.btnConfirmPassword.setOnClickListener {
            onPasswordConfirm(binding,container!!)
        }
        return binding.root
    }
    fun onSendVerificationCode(binding: FragmentForgetPasswordBinding){
        if(binding.txtPhone.text.isNullOrEmpty()) {
            binding.viewModel!!.showMadal(requireContext(), "Phone is require")
            return
        }
        binding.viewModel!!.hideKeyboard(requireContext(),requireView())
        binding.viewModel!!.setPhone(binding.txtPhone.text.toString(),requireContext())
    }
    fun onPasswordConfirm(binding: FragmentForgetPasswordBinding,container: ViewGroup){
        if(binding.txtPassword.text.isNullOrEmpty()){
            binding.viewModel!!.showMadal(requireContext(),"Password is require")
            return
        }
        if(binding.txtConfirmPassword.text.isNullOrEmpty()){
            binding.viewModel!!.showMadal(requireContext(),"Confirm password is require")
            return
        }
        if(binding.txtPassword.text.toString()!=binding.txtConfirmPassword.text.toString()){
            binding.viewModel!!.showMadal(requireContext(),"Password and Confirm password are not correct")
            return
        }
        binding.viewModel!!.hideKeyboard(requireContext(),requireView())
        binding.viewModel!!.resetPassword(requireContext(),binding.txtPassword.text.toString(),container)
    }
}
