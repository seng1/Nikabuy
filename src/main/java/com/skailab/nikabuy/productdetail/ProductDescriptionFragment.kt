package com.skailab.nikabuy.productdetail


import android.os.Bundle
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider

import com.skailab.nikabuy.databinding.FragmentProductDescriptionBinding
import com.skailab.nikabuy.factory.ProductDescriptionViewModelFactory
import com.skailab.nikabuy.room.UserDatabase
import com.skailab.nikabuy.viewModels.ProductDescriptionViewModel
import kotlin.requireNotNull as requireNotNull1

/**
 * A simple [Fragment] subclass.
 */
class ProductDescriptionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentProductDescriptionBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val application = requireNotNull1(value = this.activity).application
        val viewModelFactory = ProductDescriptionViewModelFactory(UserDatabase.getInstance(application).userDao)
        binding.viewModel = ViewModelProvider(this,viewModelFactory).get(ProductDescriptionViewModel::class.java)
        if(requireArguments()!=null){
            binding.viewModel!!.setProduct(ProductDescriptionFragmentArgs.fromBundle(requireArguments()).productDetail)
            val encodedHtml = Base64.encodeToString(binding.viewModel!!.product.value!!.description!!.toByteArray(), Base64.NO_PADDING)
            binding.webviewDescription.loadData(encodedHtml, "text/html", "base64")
        }
        // Inflate the layout for this fragment
        return  binding.root
    }


}
