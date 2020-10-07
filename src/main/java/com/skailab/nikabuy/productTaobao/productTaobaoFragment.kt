package com.skailab.nikabuy.productTaobao


import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skailab.nikabuy.LocaleHelper
import com.skailab.nikabuy.R
import com.skailab.nikabuy.Space
import com.skailab.nikabuy.adapter.ProductAdapter
import com.skailab.nikabuy.adapter.SliderAdapter
import com.skailab.nikabuy.databinding.FragmentProductTaobaoBinding
import com.skailab.nikabuy.factory.ProductTaobaoViewModelFactory
import com.skailab.nikabuy.models.Product
import com.skailab.nikabuy.models.ProductTaobao
import com.skailab.nikabuy.productdetail.ProductDetailFragmentDirections
import com.skailab.nikabuy.room.UserDatabase
import com.skailab.nikabuy.viewModels.ProductTaobaoViewModel
import com.smarteist.autoimageslider.IndicatorAnimations
import com.smarteist.autoimageslider.SliderAnimations


/**
 * A simple [Fragment] subclass.
 */
class productTaobaoFragment : Fragment() {
    var itemId:String=""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val binding = FragmentProductTaobaoBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val application = requireNotNull(value = this.activity).application
        val viewModelFactory = ProductTaobaoViewModelFactory(UserDatabase.getInstance(application).userDao)
        binding.viewModel = ViewModelProvider(this,viewModelFactory).get(ProductTaobaoViewModel::class.java)
        binding.viewModel!!.setProduct(productTaobaoFragmentArgs.fromBundle(requireArguments()).productTaobao)
        itemId=binding.viewModel!!.product.value!!.itemId!!
        binding.webView.getSettings().setLoadWithOverviewMode(true);
        binding.webView.getSettings().setUseWideViewPort(true);
        var adapter= ProductAdapter(ProductAdapter.OnClickListener {
            binding.viewModel!!.getProductDetail(requireContext(),it.itemId)
        })

        binding.viewModel!!.product.observe(viewLifecycleOwner,androidx.lifecycle.Observer { product:ProductTaobao? ->
            if(product != null){
                itemId=binding.viewModel!!.product.value!!.itemId!!
                binding.imageSlider.setSliderAdapter(SliderAdapter(context,binding.viewModel!!.product.value!!.imageUrls))
                binding.imageSlider.setIndicatorAnimation(IndicatorAnimations.WORM)
                binding.imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
                LocaleHelper.bindProductDescription(binding.webView,product.description!!)
                binding.nestedScroll.fullScroll(View.FOCUS_UP)
            }
        })
        binding.viewModel!!.suggestProducts.observe(viewLifecycleOwner,androidx.lifecycle.Observer { suggestProducts:MutableList<Product> ->
            if(suggestProducts.count()>0){
                binding.rcvProductItems.addItemDecoration(Space(suggestProducts.count(),5,true,0))
                adapter.submitList(suggestProducts)
                binding.rcvProductItems.adapter=adapter
                adapter.notifyDataSetChanged()
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
            container!!.findNavController().navigate(productTaobaoFragmentDirections.actionProductTaobaoFragmentToProducttaobaoAddCartFragment(binding.viewModel!!.product.value!!))
        }
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
