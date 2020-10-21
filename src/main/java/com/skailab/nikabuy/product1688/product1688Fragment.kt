package com.skailab.nikabuy.product1688


import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.skailab.nikabuy.LocaleHelper

import com.skailab.nikabuy.R
import com.skailab.nikabuy.Space
import com.skailab.nikabuy.adapter.ProductAdapter
import com.skailab.nikabuy.adapter.ProductRecommentAdapter
import com.skailab.nikabuy.adapter.SliderAdapter
import com.skailab.nikabuy.databinding.FragmentProduct1688Binding
import com.skailab.nikabuy.databinding.FragmentProductTaobaoBinding
import com.skailab.nikabuy.factory.Product1688ViewModelFactory
import com.skailab.nikabuy.factory.ProductTaobaoViewModelFactory
import com.skailab.nikabuy.models.Product
import com.skailab.nikabuy.models.Product1688
import com.skailab.nikabuy.models.ProductTaobao
import com.skailab.nikabuy.productTaobao.productTaobaoFragmentArgs
import com.skailab.nikabuy.productTaobao.productTaobaoFragmentDirections
import com.skailab.nikabuy.room.UserDatabase
import com.skailab.nikabuy.viewModels.Product1688ViewModel
import com.skailab.nikabuy.viewModels.ProductTaobaoViewModel
import com.smarteist.autoimageslider.IndicatorAnimations
import com.smarteist.autoimageslider.SliderAnimations

/**
 * A simple [Fragment] subclass.
 */
class product1688Fragment : Fragment() {
    var itemId:String=""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val binding = FragmentProduct1688Binding.inflate(inflater)
        binding.lifecycleOwner = this
        val application = requireNotNull(value = this.activity).application
        val viewModelFactory = Product1688ViewModelFactory(UserDatabase.getInstance(application).userDao)
        binding.viewModel = ViewModelProvider(this,viewModelFactory).get(Product1688ViewModel::class.java)
        binding.viewModel!!.setProduct(product1688FragmentArgs.fromBundle(requireArguments()).product1688Detail)
        itemId=binding.viewModel!!.product.value!!.itemId!!
        binding.webView.getSettings().setLoadWithOverviewMode(true)
        binding.webView.getSettings().setUseWideViewPort(true);
        var adapter= ProductRecommentAdapter(ProductRecommentAdapter.OnClickListener {
            binding.viewModel!!.getProductDetail(requireContext(),it.itemId)
        })
        binding.viewModel!!.product.observe(viewLifecycleOwner,androidx.lifecycle.Observer { product: Product1688? ->
            if(product != null){
                binding.imageSlider.setSliderAdapter(SliderAdapter(context,binding.viewModel!!.product.value!!.imageUrls))
                binding.imageSlider.setIndicatorAnimation(IndicatorAnimations.WORM)
                binding.imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
                LocaleHelper.bindProductDescription(binding.webView,product.description!!)
                binding.nestedScroll.fullScroll(View.FOCUS_UP)
                binding.textViewMinimumQty.text=product.minQty.toString()
                if(product.priceRanges!=null && product.priceRanges!!.count()>0){
                    binding.lnPriceTitle.visibility=View.VISIBLE
                    binding.lnPriceValue.visibility=View.VISIBLE
                    binding.viewModel!!.bindPrice(binding.priceRange1,product.priceRanges!![0].price)
                    binding.priceRange1.visibility=View.VISIBLE
                    binding.priceRangeValue1.text=product.priceRanges!![0].description
                    binding.priceRangeValue1.visibility=View.VISIBLE
                    if(product.priceRanges!!.count()>=2){
                        binding.viewModel!!.bindPrice(binding.priceRange2,product.priceRanges!![1].price)
                        binding.priceRange2.visibility=View.VISIBLE
                        binding.priceRangeValue2.text=product.priceRanges!![1].description
                        binding.priceRangeValue2.visibility=View.VISIBLE
                    }
                    else{
                        binding.priceRange2.visibility=View.GONE
                        binding.priceRangeValue2.visibility=View.GONE
                    }
                    if(product.priceRanges!!.count()>=3){
                        binding.viewModel!!.bindPrice(binding.priceRange3,product.priceRanges!![2].price)
                        binding.priceRange3.visibility=View.VISIBLE
                        binding.priceRangeValue3.text=product.priceRanges!![2].description
                        binding.priceRangeValue3.visibility=View.VISIBLE
                    }
                    else{
                        binding.priceRange3.visibility=View.GONE
                        binding.priceRangeValue3.visibility=View.GONE
                    }
                }
                else{
                    binding.lnPriceTitle.visibility=View.GONE
                    binding.priceRange1.visibility=View.GONE
                    binding.priceRange2.visibility=View.GONE
                    binding.priceRange3.visibility=View.GONE
                    binding.lnPriceValue.visibility=View.GONE
                    binding.priceRangeValue1.visibility=View.GONE
                    binding.priceRangeValue2.visibility=View.GONE
                    binding.priceRangeValue2.visibility=View.GONE
                }
                itemId=binding.viewModel!!.product.value!!.itemId!!
            }
        })
        binding.viewModel!!.isUserRequested.observe(viewLifecycleOwner,androidx.lifecycle.Observer { isUserRequest:Boolean? ->
            if(isUserRequest!!){
                if(binding.viewModel!!.userEntity.value==null){
                    container!!.findNavController().navigate(R.id.accountFragment)
                }
                else{
                    binding.viewModel!!.getSuggestProduct(requireContext())
                }
            }
        })
        binding.btnAddToCart.setOnClickListener {
            container!!.findNavController().navigate(product1688FragmentDirections.actionProduct1688FragmentToProduct1688AddCartFragment(binding.viewModel!!.product.value!!))
        }
        binding.viewModel!!.suggestProducts.observe(viewLifecycleOwner,androidx.lifecycle.Observer { suggestProducts:MutableList<Product> ->
            if(suggestProducts.count()>0){
                binding.rcvProductItems.addItemDecoration(Space(suggestProducts.count(),5,true,0))
                adapter.submitList(suggestProducts)
                binding.rcvProductItems.adapter=adapter
                adapter.notifyDataSetChanged()
            }
        })

        return binding.root
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.share_menu, menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.share->{
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.type="text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT, "https://nikabuy.com/cart?itemId="+itemId)
                startActivity(Intent.createChooser(shareIntent,getString(R.string.share)))
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
