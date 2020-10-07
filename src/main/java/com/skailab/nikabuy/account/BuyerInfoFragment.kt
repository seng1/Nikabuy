package com.skailab.nikabuy.account


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController

import com.skailab.nikabuy.R
import com.skailab.nikabuy.databinding.FragmentBuyerInfoBinding
import com.skailab.nikabuy.factory.BuyerViewModelFactory
import com.skailab.nikabuy.room.UserDatabase
import com.skailab.nikabuy.viewModels.BuyerViewModel
import kotlin.requireNotNull as requireNotNull1

/**
 * A simple [Fragment] subclass.
 */
class BuyerInfoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentBuyerInfoBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val application = requireNotNull1(value = this.activity).application
        val viewModelFactory = BuyerViewModelFactory(UserDatabase.getInstance(application).userDao)
        binding.viewModel = ViewModelProvider(this,viewModelFactory).get(BuyerViewModel::class.java)
        binding.viewModel!!.isUserRequested.observe(viewLifecycleOwner,androidx.lifecycle.Observer { isUserRequest:Boolean? ->
            if(isUserRequest!!){
                if(binding.viewModel!!.userEntity.value==null){
                    container!!.findNavController().navigate(R.id.accountFragment)
                }
                else
                {
                    binding.viewModel!!.getBuyer(requireContext())
                }
            }
        })
        binding.btnSave.setOnClickListener({
            binding.viewModel!!.hideKeyboard(requireContext(),requireView())
            binding.viewModel!!.updateBuyer(requireContext())
        })
        // Inflate the layout for this fragment
        return binding.root
    }
}
