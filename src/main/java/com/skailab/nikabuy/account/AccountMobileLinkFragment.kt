package com.skailab.nikabuy.account


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder

import com.skailab.nikabuy.R
import com.skailab.nikabuy.databinding.FragmentAccountMobileLinkBinding
import com.skailab.nikabuy.factory.MobileLinkAccountViewModelFactory
import com.skailab.nikabuy.room.UserDatabase
import com.skailab.nikabuy.viewModels.MobileLinkAccountViewModel

/**
 * A simple [Fragment] subclass.
 */
class AccountMobileLinkFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val binding = FragmentAccountMobileLinkBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val application = requireNotNull(value = this.activity).application
        val viewModelFactory = MobileLinkAccountViewModelFactory(UserDatabase.getInstance(application).userDao)
        binding.viewModel = ViewModelProvider(this,viewModelFactory).get(MobileLinkAccountViewModel::class.java)
        binding.registerButton.setOnClickListener {
            onRegister(binding)
        }
        binding.confirmVerificationButton.setOnClickListener {
            onConfirmVerification(binding,container!!)
        }
        binding.resentVerifcationCodeButton.setOnClickListener {
            binding.viewModel!!.sendVerification(requireContext())
        }
        // Inflate the layout for this fragment
        return binding.root
    }
    fun onRegister(binding: FragmentAccountMobileLinkBinding){
        if(binding.txtPhone.text.isNullOrEmpty()){
            MaterialAlertDialogBuilder(context)
                .setTitle(resources.getString(R.string.app_name))
                .setMessage(resources.getString(R.string.phone_require))
                .setNegativeButton(resources.getString(R.string.ok)) { _, _ ->
                }.show()
            return
        }
        if(binding.txtPassword.text.isNullOrEmpty()){
            MaterialAlertDialogBuilder(context)
                .setTitle(resources.getString(R.string.app_name))
                .setMessage(resources.getString(R.string.password_require))
                .setNegativeButton(resources.getString(R.string.ok)) { _, which ->
                }.show()
            return
        }
        if(binding.txtConfirmPassword.text.isNullOrEmpty()){
            MaterialAlertDialogBuilder(context)
                .setTitle(resources.getString(R.string.app_name))
                .setMessage(resources.getString(R.string.confirm_password_require))
                .setNegativeButton(resources.getString(R.string.ok)) { dialog, which ->
                }.show()
            return
        }
        if(binding.txtPassword.text.toString()!=binding.txtConfirmPassword.text.toString()){
            MaterialAlertDialogBuilder(context)
                .setTitle(resources.getString(R.string.app_name))
                .setMessage(resources.getString(R.string.password_and_confirm_password_not_the_same))
                .setNegativeButton(resources.getString(R.string.ok)) { dialog, which ->
                }.show()
            return
        }
        binding.viewModel!!.hideKeyboard(requireContext(),requireView())
        binding.viewModel!!.sendSms(requireContext(),binding.txtPhone.text.toString(),binding.txtPassword.text.toString())
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_menu, menu)
    }
    fun onConfirmVerification(binding: FragmentAccountMobileLinkBinding,container: ViewGroup){
        if(binding.confirmVerification.text.toString()!=binding.viewModel!!._verificationCode){
            MaterialAlertDialogBuilder(context)
                .setTitle(resources.getString(R.string.app_name))
                .setMessage(resources.getString(R.string.verification_code_not_correct))
                .setNegativeButton(resources.getString(R.string.ok)) { dialog, which ->
                }.show()
            return
        }
        binding.viewModel!!.hideKeyboard(requireContext(),requireView())
        binding.viewModel!!.onBind(requireContext(),container)
    }


}
