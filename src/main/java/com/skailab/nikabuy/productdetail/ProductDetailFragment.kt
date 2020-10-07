package com.skailab.nikabuy.productdetail


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.skailab.nikabuy.adapter.SliderAdapter
import com.skailab.nikabuy.databinding.FragmentProductDetailBinding
import com.skailab.nikabuy.factory.ProductDetailViewModelFactory
import com.skailab.nikabuy.room.UserDatabase
import com.skailab.nikabuy.viewModels.ProductDetailViewModel
import com.smarteist.autoimageslider.IndicatorAnimations
import com.smarteist.autoimageslider.SliderAnimations
import kotlin.requireNotNull as requireNotNull1

/**
 * A simple [Fragment] subclass.
 */
class ProductDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentProductDetailBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val application = requireNotNull1(value = this.activity).application
        val viewModelFactory = ProductDetailViewModelFactory(UserDatabase.getInstance(application).userDao)
        binding.viewModel = ViewModelProvider(this,viewModelFactory).get(ProductDetailViewModel::class.java)
        binding.viewModel!!.setProduct(ProductDetailFragmentArgs.fromBundle(requireArguments()).productDetail)
        binding.imageSlider.setSliderAdapter(SliderAdapter(context,binding.viewModel!!.product.value!!.imageUrls))
        binding.imageSlider.setIndicatorAnimation(IndicatorAnimations.WORM)
        binding.imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)

        binding.btnScore.setOnClickListener {
            container!!.findNavController().navigate(ProductDetailFragmentDirections.actionProductDetailFragmentToProductScoreFragment(binding.viewModel!!.product.value!!))
        }
        binding.btnProductDescription.setOnClickListener {
            container!!.findNavController().navigate(ProductDetailFragmentDirections.actionProductDetailFragmentToProductDescriptionFragment(binding.viewModel!!.product.value!!))
        }
        binding.btnProductRecomend.setOnClickListener {
            container!!.findNavController().navigate(ProductDetailFragmentDirections.actionProductDetailFragmentToRecommentFragment(binding.viewModel!!.product.value!!))
        }
        binding.btnAddToCart.setOnClickListener {

            container!!.findNavController().navigate(ProductDetailFragmentDirections.actionProductDetailFragmentToAddCartFragment(binding.viewModel!!.product.value!!))

        }
        return  binding.root
    }
}
