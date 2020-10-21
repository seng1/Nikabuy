package com.skailab.nikabuy.payment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.skailab.nikabuy.R
import com.skailab.nikabuy.databinding.FragmentNewPaymentPasswordBinding
import com.skailab.nikabuy.factory.NewPaymentPasswordViewModelFactory
import com.skailab.nikabuy.room.UserDatabase
import com.skailab.nikabuy.viewModels.NewPaymentPasswordViewModel

/**
 * A simple [Fragment] subclass.
 */
class NewPaymentPasswordFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentNewPaymentPasswordBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val application = requireNotNull(value = this.activity).application
        val viewModelFactory = NewPaymentPasswordViewModelFactory(UserDatabase.getInstance(application).userDao)
        binding.viewModel = ViewModelProvider(this,viewModelFactory).get(NewPaymentPasswordViewModel::class.java)
        binding.btnSave.setOnClickListener {
            onSubmit(binding,container!!)
        }
        return  binding.root
    }
    fun onSubmit(binding: FragmentNewPaymentPasswordBinding, container: ViewGroup){
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
        binding.viewModel!!.Save(requireContext(),container,binding.txtPaymentPassword.text.toString(),requireActivity())
    }

}
