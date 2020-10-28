package com.skailab.nikabuy.productTaobao

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skailab.nikabuy.R
import com.skailab.nikabuy.Space
import com.skailab.nikabuy.adapter.ProductTaobaoSkuAdapter
import com.skailab.nikabuy.bottomfragment.BottomImageFragment
import com.skailab.nikabuy.databinding.FragmentProducttaobaoAddCartBinding
import com.skailab.nikabuy.factory.ProductTaobaoAddCartViewModelFactory
import com.skailab.nikabuy.models.SkuItemModel
import com.skailab.nikabuy.room.UserDatabase
import com.skailab.nikabuy.viewModels.ProductTaoboaAddCartViewModel

/**
 * A simple [Fragment] subclass.
 */
class productTaobaoAddCartFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentProducttaobaoAddCartBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val application = requireNotNull(value = this.activity).application
        val viewModelFactory = ProductTaobaoAddCartViewModelFactory(UserDatabase.getInstance(application).userDao)
        binding.viewModel = ViewModelProvider(this,viewModelFactory).get(
            ProductTaoboaAddCartViewModel::class.java)
        binding.viewModel!!.setProduct(productTaobaoFragmentArgs.fromBundle(requireArguments()).productTaobao)
        if(binding.viewModel!!.product.value!!.skuModels!=null && binding.viewModel!!.product.value!!.skuModels!!.count()>0){
            binding.viewModel!!.product.value!!.skuModels!!.forEach {
                val textView = TextView(requireContext())
                textView.text =it.title
                textView.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                binding.lnSku.addView(textView)
                it.skuItemModels!!.forEach{t:SkuItemModel ->
                   t.skuText=it.orignalTitle
                    t.isSelected=false
                }

                var rc = RecyclerView(requireContext())
                var rcLayoutManager=LinearLayoutManager(requireContext())
                rcLayoutManager.orientation=RecyclerView.HORIZONTAL
                rc.addItemDecoration(Space(it.skuItemModels!!.count(),5,true,0))
                rc.setLayoutManager(rcLayoutManager)
                val adapter= ProductTaobaoSkuAdapter(ProductTaobaoSkuAdapter.OnClickListener { skuItemModel: SkuItemModel, productTaobaoSkuAdapter: ProductTaobaoSkuAdapter ->
                    binding.viewModel!!.product.value!!.skuModels!!.forEach {
                        if(it.orignalTitle==skuItemModel.skuText){
                            it.skuItemModels!!.forEach {t: SkuItemModel->
                                t.isSelected=false
                            }
                            it.selectedSkuItem=skuItemModel
                        }
                    }
                    skuItemModel.isSelected=true
                    productTaobaoSkuAdapter.notifyDataSetChanged()
                    if(skuItemModel.isHasImage!!){
                        binding.viewModel!!.product.value!!.imageUrl=skuItemModel.imageUrl
                        binding.viewModel!!.bindImage(binding.googdImage,binding.viewModel!!.product.value!!.imageUrl)
                    }
                    checkIfAllSkuSelected(binding)
                })
                rc.adapter=adapter
                adapter.submitList(it.skuItemModels!!)
                binding.lnSku.addView(rc)
            }
        }
        binding.googdImage.setOnClickListener{
            val bottomSheet = BottomImageFragment(binding.viewModel!!.product.value!!.imageUrl!!,binding.viewModel!!.product.value!!.title)
            bottomSheet.show(requireActivity().supportFragmentManager, "image")
        }
        binding.tvStockText.visibility=View.GONE
        if(binding.viewModel!!.product.value!!.skuModels== null || binding.viewModel!!.product.value!!.skuModels!!.count()==0){
            binding.viewModel!!.bindPrice(binding.price,binding.viewModel!!.product.value!!.unitPrice)
            binding.tvStockText.visibility=View.VISIBLE
            binding.textViewStock.text=binding.viewModel!!.product.value!!.masterQuantity!!.toString()
        }
        binding.plusBtn.setOnClickListener {
            binding.viewModel!!.product.value!!.quantity=binding.viewModel!!.product.value!!.quantity!!+1
            binding.qty.setText(binding.viewModel!!.product.value!!.quantity!!.toString())
            calculateTotal(binding)
        }
        binding.minusBtn.setOnClickListener {
            if(binding.viewModel!!.product.value!!.quantity!!>0){
                binding.viewModel!!.product.value!!.quantity=binding.viewModel!!.product.value!!.quantity!!-1
                binding.qty.setText(binding.viewModel!!.product.value!!.quantity!!.toString())
                calculateTotal(binding)
            }
        }
        binding.qty.addTextChangedListener(object:
            TextWatcher {override fun afterTextChanged(s: Editable?) {
        }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(binding.qty.text.isNullOrEmpty()){
                   binding.viewModel!!.product.value!!.quantity=0
                    calculateTotal(binding)
                }
                else{
                   binding.viewModel!!.product.value!!.quantity=binding.qty.text.toString().toInt()
                    calculateTotal(binding)
                }
            }
        })
        binding.btnCart.setOnClickListener {
             addCart(binding,container)
        }
        binding.btnBuyNow.setOnClickListener {
            addToPurchase(binding,container)
        }
        binding.viewModel!!.isUserRequested.observe(viewLifecycleOwner,androidx.lifecycle.Observer { isUserRequest:Boolean? ->
            if(isUserRequest!!){
                if(binding.viewModel!!.userEntity.value==null){
                    container!!.findNavController().navigate(R.id.accountFragment)
                }
            }
        })
        calculateTotal(binding)
        return binding.root
    }
    private fun addCart(binding: FragmentProducttaobaoAddCartBinding,container: ViewGroup?){
       if(binding.viewModel!!.product.value!!.quantity!!<=0){
           binding.viewModel!!.showMadal(requireContext(),getString(R.string.quantity_require))
           return
       }
       if(binding.viewModel!!.product.value!!.skuModels!=null && binding.viewModel!!.product.value!!.skuModels!!.count()>0){
           if(binding.viewModel!!.product.value!!.orginalSelectedSkuText==null || binding.viewModel!!.product.value!!.orginalSelectedSkuText!!.isEmpty()){
               binding.viewModel!!.showMadal(requireContext(),getString(R.string.please_select_product_variant))
               return
           }
       }
        binding.viewModel!!.product.value!!.description=binding.messageToCustomer.text.toString()
        binding.viewModel!!.hideKeyboard(requireContext(),requireView())
        binding.viewModel!!.onAddCart(requireContext(),container!!)
    }
    private  fun addToPurchase(binding: FragmentProducttaobaoAddCartBinding,container: ViewGroup?){
        if(binding.viewModel!!.product.value!!.quantity!!<=0){
            binding.viewModel!!.showMadal(requireContext(),getString(R.string.quantity_require))
            return
        }
        if(binding.viewModel!!.product.value!!.skuModels!=null && binding.viewModel!!.product.value!!.skuModels!!.count()>0){
            if(binding.viewModel!!.product.value!!.orginalSelectedSkuText==null || binding.viewModel!!.product.value!!.orginalSelectedSkuText!!.isEmpty()){
                binding.viewModel!!.showMadal(requireContext(),getString(R.string.please_select_product_variant))
                return
            }
        }
        binding.viewModel!!.product.value!!.description=binding.messageToCustomer.text.toString()
        binding.viewModel!!.hideKeyboard(requireContext(),requireView())
        binding.viewModel!!.addPurchaseCart(requireContext(),container!!)
    }
    private fun checkIfAllSkuSelected(binding:FragmentProducttaobaoAddCartBinding){
        if(binding.viewModel!!.product.value!!.skuModels!=null && binding.viewModel!!.product.value!!.skuModels!!.count()>0){
            var skuMapText =""
            var isAllSkuSelected:Boolean=true
            binding.viewModel!!.product.value!!.skuModels!!.forEach {
                if(it.selectedSkuItem!=null){
                    if(skuMapText!=""){
                        skuMapText+=";"
                    }
                    skuMapText+=it.orignalTitle+":"+it.selectedSkuItem!!.orignalTitle
                }
                else{
                    isAllSkuSelected= false
                    return
                }
            }
            if(isAllSkuSelected){
                binding.viewModel!!.product.value!!.skuMapModels!!.forEach {
                    if(it.originalMapKey==skuMapText){
                        binding.viewModel!!.product.value!!.orginalSelectedSkuText=it.originalMapKey
                        binding.viewModel!!.product.value!!.selectedSkuText=it.mapKey
                        binding.viewModel!!.product.value!!.unitPriceInChn=it.unitPriceInChn
                        binding.viewModel!!.product.value!!.unitPrice=it.unitPrice
                        binding.viewModel!!.bindPrice(binding.price,it.unitPrice)
                        binding.textViewStock.text=it.stockQuantity.toString()
                        binding.tvStockText.visibility=View.VISIBLE
                        calculateTotal(binding)
                        return
                    }
                }
            }
        }
    }
    private fun  calculateTotal(binding:FragmentProducttaobaoAddCartBinding){
        binding.textViewUnitPrice.text=binding.viewModel!!.priceFormat(binding.viewModel!!.product.value!!.unitPrice)
        var total:Double=binding.viewModel!!.product.value!!.unitPrice!!*binding.viewModel!!.product.value!!.quantity!!
        binding.textViewTotal.text=binding.viewModel!!.priceFormat(total)
    }

}
