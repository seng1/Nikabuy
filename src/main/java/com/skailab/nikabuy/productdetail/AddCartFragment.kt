package com.skailab.nikabuy.productdetail


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.skailab.nikabuy.R
import com.skailab.nikabuy.adapter.ProductSkuItemAdapter
import com.skailab.nikabuy.bottomfragment.BottomImageFragment
import com.skailab.nikabuy.databinding.FragmentAddCartBinding
import com.skailab.nikabuy.factory.AddCartViewModelFactory
import com.skailab.nikabuy.room.UserDatabase
import com.skailab.nikabuy.viewModels.AddCartViewModel as AddCartViewModel1
import kotlin.requireNotNull as requireNotNull1


/**
 * A simple [Fragment] subclass.
 */
class AddCartFragment() :Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentAddCartBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val application = requireNotNull1(value = this.activity).application
        val viewModelFactory = AddCartViewModelFactory(UserDatabase.getInstance(application).userDao)
        binding.viewModel = ViewModelProvider(this,viewModelFactory).get(AddCartViewModel1::class.java)
        if(requireArguments()!=null){
            binding.viewModel!!.setProduct(ProductDescriptionFragmentArgs.fromBundle(requireArguments()).productDetail)
        }
        val adapter= ProductSkuItemAdapter(
            ProductSkuItemAdapter.OnImageClickListener {
                val bottomSheet = BottomImageFragment(it.product.value!!.imageUrl!!,it.product.value!!.skuText!!)
                bottomSheet.show(requireActivity().supportFragmentManager, "image")
            },
            ProductSkuItemAdapter.OnTextChangeListener{product, index ->
                binding.viewModel!!.calculateTotal(requireContext())
            }
        )
        binding.rcvProductItems.adapter =adapter
        binding.googdImage.setOnClickListener({
            val bottomSheet = BottomImageFragment(binding.viewModel!!.product.value!!.imageUrl,binding.viewModel!!.product.value!!.title)
            bottomSheet.show(requireActivity().supportFragmentManager, "image")
        })
        binding.viewModel!!.calculateTotal(requireContext())
        binding.messageToCustomer.addTextChangedListener(object:
            TextWatcher {override fun afterTextChanged(s: Editable?) {
        }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
               binding.viewModel!!.setMessageToCustomer(binding.messageToCustomer.text.toString())
            }
        })
        binding.messageToCustomer2.addTextChangedListener(object:
            TextWatcher {override fun afterTextChanged(s: Editable?) {
        }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.viewModel!!.setMessageToCustomer(binding.messageToCustomer2.text.toString())
            }
        })
        binding.quantity.addTextChangedListener(object:
            TextWatcher {override fun afterTextChanged(s: Editable?) {
        }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(binding.quantity.text.isNullOrEmpty()){
                    binding.viewModel!!.setQuantity(null,requireContext())
                }
                else{
                    binding.viewModel!!.setQuantity(binding.quantity.text.toString().toInt(),requireContext())
                }
            }
        })
        binding.btnCart.setOnClickListener({
            onAddCart(binding.viewModel!!,container!!)
        })
        binding.btnBuyNow.setOnClickListener({
            onBuyNowCart(binding.viewModel!!,container!!)
        })
        // Inflate the layout for this fragment
        return  binding.root
    }
    fun onAddCart(viewModel: com.skailab.nikabuy.viewModels.AddCartViewModel,container: ViewGroup){
        if(viewModel.total.value==0.0){
            viewModel!!.showMadal(requireContext(),getString(R.string.quantity_require))
            return
        }
        viewModel.hideKeyboard(requireContext(),requireView())
        viewModel.onAddCart(requireContext(),container,requireActivity())
    }
    fun onBuyNowCart(viewModel: com.skailab.nikabuy.viewModels.AddCartViewModel,container: ViewGroup){
        if(viewModel.total.value==0.0){
            viewModel!!.showMadal(requireContext(),getString(R.string.quantity_require))
            return
        }
        viewModel.hideKeyboard(requireContext(),requireView())
        viewModel.onBuyNow(requireContext(),container)
    }
}
