package com.skailab.nikabuy.bottomfragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

import com.skailab.nikabuy.R
import com.skailab.nikabuy.databinding.FragmentBottomImageBinding
import com.skailab.nikabuy.databinding.FragmentProductDescriptionBinding
import com.skailab.nikabuy.factory.BottomImageViewModelFactory
import com.skailab.nikabuy.factory.ProductDescriptionViewModelFactory
import com.skailab.nikabuy.room.UserDatabase
import com.skailab.nikabuy.viewModels.BottomImageViewModel
import com.skailab.nikabuy.viewModels.ProductDescriptionViewModel
import kotlinx.android.synthetic.main.fragment_product_detail.*

/**
 * A simple [Fragment] subclass.
 */
class BottomImageFragment(val imageUrl:String,val description:String) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentBottomImageBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val application = requireNotNull(value = this.activity).application
        val viewModelFactory = BottomImageViewModelFactory(UserDatabase.getInstance(application).userDao)
        binding.viewModel = ViewModelProvider(this,viewModelFactory).get(BottomImageViewModel::class.java)
        binding.viewModel!!.setImage(imageUrl,description)
        // Inflate the layout for this fragment
        return binding.root
    }


}
