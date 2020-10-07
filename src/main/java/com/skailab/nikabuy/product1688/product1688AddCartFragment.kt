package com.skailab.nikabuy.product1688


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skailab.nikabuy.R
import com.skailab.nikabuy.Space
import com.skailab.nikabuy.adapter.Product1688MainSkuAdapter
import com.skailab.nikabuy.adapter.Product1688SkuItemAdapter
import com.skailab.nikabuy.bottomfragment.BottomImageFragment
import com.skailab.nikabuy.databinding.FragmentProduct1688AddCartBinding
import com.skailab.nikabuy.factory.Product1688AddCartViewModelFactory
import com.skailab.nikabuy.models.PriceRange
import com.skailab.nikabuy.models.Sku1688
import com.skailab.nikabuy.models.SkuItem1688Rvc
import com.skailab.nikabuy.room.UserDatabase
import com.skailab.nikabuy.viewModels.Product1688AddCartViewModel

/**
 * A simple [Fragment] subclass.
 */
class product1688AddCartFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentProduct1688AddCartBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val application = requireNotNull(value = this.activity).application
        val viewModelFactory = Product1688AddCartViewModelFactory(UserDatabase.getInstance(application).userDao)
        binding.viewModel = ViewModelProvider(this,viewModelFactory).get(
            Product1688AddCartViewModel::class.java)
        val product=product1688AddCartFragmentArgs.fromBundle(requireArguments()).product1688
        binding.viewModel!!.setProduct(product)
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
        if(product.mainSku!=null||(binding.viewModel!!.product.value!!.skuItems!=null && product.skuItems!!.count()>0)){
            binding.lnQty.visibility=View.GONE
            var rc:RecyclerView?=null
            if(product.mainSku!=null){
                binding.tvMainSkuText.visibility=View.VISIBLE
                binding.tvMainSkuText.text=product.mainSku!!.propertyTitle
                binding.rcvMainSku.visibility=View.VISIBLE
                val adapter= Product1688MainSkuAdapter(Product1688MainSkuAdapter.OnClickListener { skuItemModel: Sku1688 ->
                    product.mainSku!!.skus!!.forEach {
                        it.isSelected=false
                    }
                    if(skuItemModel.isHasImage!!){
                        product.imageUrl=skuItemModel.imageUrl
                        binding.viewModel!!.bindImage(binding.googdImage,binding.viewModel!!.product.value!!.imageUrl)
                    }
                    skuItemModel.isSelected=true
                    binding.viewModel!!.skuItems.value!!.clear()
                    binding.rcvMainSku.adapter!!.notifyDataSetChanged()
                    val adapter3= Product1688SkuItemAdapter(
                        Product1688SkuItemAdapter.OnClickListener { skuItemModel: SkuItem1688Rvc ->
                            val bottomSheet = BottomImageFragment(skuItemModel.sku.imageUrl!!,skuItemModel.sku.skuText!!)
                            bottomSheet.show(requireActivity().supportFragmentManager, "image")

                        },
                        Product1688SkuItemAdapter.OnQtyChangeListener { skuItemModel: SkuItem1688Rvc ->
                            if(product.priceRanges!=null  && product.priceRanges!!.count()>0){
                                resetPriceIfHasPriceRange(binding)
                                binding.viewModel!!.skuItems.value!!.forEach {
                                    if(it.TextViewPrice!=null){
                                        binding.viewModel!!.bindPrice(it.TextViewPrice!!,it.sku.price)
                                    }
                                }
                            }
                            setTotalToText(binding)
                        }
                    )
                    var rc1 = RecyclerView(requireContext())
                    var rcLayoutManager= LinearLayoutManager(requireContext())
                    rcLayoutManager.orientation= RecyclerView.VERTICAL
                    rc1.addItemDecoration(Space(skuItemModel.skuItems!!.count(),5,true,0))
                    rc1.setLayoutManager(rcLayoutManager)
                    rc1.adapter=adapter3
                    binding.viewModel!!.skuItems.value!!.clear()
                    skuItemModel.skuItems!!.forEach {
                        binding.viewModel!!.skuItems.value!!.add(SkuItem1688Rvc(it))
                    }
                    adapter3.submitList(binding.viewModel!!.skuItems.value!!)
                    binding.LinearLayout1.addView(rc1)
                    if(rc!=null){
                        binding.LinearLayout1.removeView(rc)
                    }
                    rc=rc1


                })
                binding.rcvMainSku.addItemDecoration(Space(product.mainSku!!.skus!!.count(),5,true,0))
                binding.rcvMainSku.adapter=adapter
                adapter.submitList(product.mainSku!!.skus!!)
                product.skuItems=mutableListOf()
                binding.rcvMainSku.addItemDecoration(Space(product.mainSku!!.skus!![0].skuItems!!.count(),5,true,0))
            }
            else{
                binding.tvMainSkuText.visibility=View.GONE
                binding.rcvMainSku.visibility=View.GONE
                binding.rcvMainSku.addItemDecoration(Space(product.skuItems!!.count(),5,true,0))
                val adapter3= Product1688SkuItemAdapter(Product1688SkuItemAdapter.OnClickListener { skuItemModel: SkuItem1688Rvc ->
                    val bottomSheet = BottomImageFragment(skuItemModel.sku.imageUrl!!,skuItemModel.sku.skuText!!)
                    bottomSheet.show(requireActivity().supportFragmentManager, "image")

                },Product1688SkuItemAdapter.OnQtyChangeListener { skuItemModel: SkuItem1688Rvc ->
                    if(product.priceRanges!=null  && product.priceRanges!!.count()>0){
                        resetPriceIfHasPriceRange(binding)
                        binding.viewModel!!.skuItems.value!!.forEach {
                            if(it.TextViewPrice!=null){
                                binding.viewModel!!.bindPrice(it.TextViewPrice!!,it.sku.price)
                            }
                        }
                    }
                    setTotalToText(binding)
                })
                var rc = RecyclerView(requireContext())
                var rcLayoutManager= LinearLayoutManager(requireContext())
                rcLayoutManager.orientation= RecyclerView.VERTICAL
                rc.addItemDecoration(Space(product.skuItems!!.count(),5,true,0))
                rc.setLayoutManager(rcLayoutManager)
                rc.adapter=adapter3
                binding.viewModel!!.skuItems.value!!.clear()
                product.skuItems!!.forEach {
                    binding.viewModel!!.skuItems.value!!.add(SkuItem1688Rvc(it))
                }
                adapter3.submitList(binding.viewModel!!.skuItems.value!!)
                binding.LinearLayout1.addView(rc)

            }
        }
        else{
            binding.lnQty.visibility=View.VISIBLE
            binding.qty.setText(product.quantity.toString())
            binding.textViewStock.text=product.masterQuantity.toString()
        }

        binding.googdImage.setOnClickListener {
            val bottomSheet = BottomImageFragment(product.imageUrl!!,product.title)
            bottomSheet.show(requireActivity().supportFragmentManager, "image")
        }
        binding.plusBtn.setOnClickListener {
            product.quantity=product.quantity!!+1
            binding.qty.setText(product.quantity!!.toString())
            getTotalQty(binding)
            resetPriceIfHasPriceRange(binding)
            setTotalToText(binding)
        }
        binding.minusBtn.setOnClickListener {
            if(product.quantity!!>0){
                product.quantity=product.quantity!!-1
                binding.qty.setText(product.quantity!!.toString())
                getTotalQty(binding)
                resetPriceIfHasPriceRange(binding)
                setTotalToText(binding)
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
                    getTotalQty(binding)
                    resetPriceIfHasPriceRange(binding)
                    setTotalToText(binding)
                }
                else{
                    binding.viewModel!!.product.value!!.quantity=binding.qty.text.toString().toInt()
                    getTotalQty(binding)
                    resetPriceIfHasPriceRange(binding)
                    setTotalToText(binding)
                }
            }
        })
        binding.btnCart.setOnClickListener {
            addCart(binding,container)
        }
        binding.btnBuyNow.setOnClickListener {
            addToPurchase(binding,container)
        }
        setTotalToText(binding)
        return  binding.root
    }
    private fun resetPriceIfHasPriceRange(binding: FragmentProduct1688AddCartBinding){

        if(binding.viewModel!!.product.value!!.priceRanges!=null && binding.viewModel!!.product.value!!.priceRanges!!.count()>0){
            var priceRange=getPriceFromPriceRange(binding)
            if(binding.viewModel!!.product.value!!.mainSku!=null){
                binding.viewModel!!.product.value!!.mainSku!!.skus!!.forEach {
                    it.skuItems!!.forEach {
                        it.price=priceRange.price
                        it.unitPriceInChn=priceRange.unitPriceInChn
                    }
                }
            }
            if(binding.viewModel!!.product.value!!.skuItems!=null){
                binding.viewModel!!.product.value!!.skuItems!!.forEach {
                    it.price=priceRange.price
                    it.unitPriceInChn=priceRange.unitPriceInChn
                }
            }
            binding.viewModel!!.product.value!!.unitPrice=priceRange.price
            binding.viewModel!!.product.value!!.unitPriceInChn=priceRange.unitPriceInChn
            binding.viewModel!!.product.value!!.discountPriceRangetext=binding.viewModel!!.priceFormat(binding.viewModel!!.product.value!!.unitPrice)
            binding.price.text=binding.viewModel!!.product.value!!.discountPriceRangetext
        }
    }
    private fun getPriceFromPriceRange(binding: FragmentProduct1688AddCartBinding):PriceRange{
        val totalQty =getTotalQty(binding)
        var priceRange=binding.viewModel!!.product.value!!.priceRanges!![0]
        binding.viewModel!!.product.value!!.priceRanges!!.forEach {
            if(it.fromQuantity!!<=totalQty && it.toQuantity!!>=totalQty){
                priceRange=it
            }
        }
        return  priceRange
    }
    private fun getTotalQty(binding: FragmentProduct1688AddCartBinding):Int{
       binding.viewModel!!.totalQty=0
        if(binding.viewModel!!.product.value!!.mainSku!=null){
            binding.viewModel!!.product.value!!.mainSku!!.skus!!.forEach {
                it.skuItems!!.forEach {
                    if(it.quantity!!>0){
                        binding.viewModel!!.totalQty=binding.viewModel!!.totalQty+it.quantity!!
                    }
                }
            }
            return  binding.viewModel!!.totalQty
        }
        if(binding.viewModel!!.product.value!!.skuItems!=null && binding.viewModel!!.product.value!!.skuItems!!.count()>0){
            binding.viewModel!!.product.value!!.skuItems!!.forEach {
                if(it.quantity!!>0){
                    binding.viewModel!!.totalQty=binding.viewModel!!.totalQty+it.quantity!!
                }
            }
            return binding.viewModel!!.totalQty
        }
        binding.viewModel!!.totalQty=binding.viewModel!!.product.value!!.quantity!!
        return binding.viewModel!!.totalQty
    }
    private fun setTotalToText(binding: FragmentProduct1688AddCartBinding){
        binding.viewModel!!.totalQty=0
        var total:Double=0.0
        if(binding.viewModel!!.product.value!!.mainSku!=null){
            binding.viewModel!!.product.value!!.mainSku!!.skus!!.forEach {
                it.skuItems!!.forEach {
                    if(it.quantity!!>0){
                        binding.viewModel!!.totalQty+=it.quantity!!
                        total+=it.quantity!!*it.price!!
                    }
                }
            }
            binding.textViewTotalQty.text=binding.viewModel!!.totalQty.toString()
            binding.viewModel!!.bindPrice(binding.textViewTotalamount,total)
            return
        }
        if(binding.viewModel!!.skuItems.value!=null&&binding.viewModel!!.skuItems.value!!.count()>0){
            binding.viewModel!!.skuItems.value!!.forEach {
                binding.viewModel!!.totalQty+=it.sku.quantity!!
                total+=it.sku.quantity!!*it.sku.price!!
            }
            binding.textViewTotalQty.text=binding.viewModel!!.totalQty.toString()
            binding.viewModel!!.bindPrice(binding.textViewTotalamount,total)
            return
        }
        binding.viewModel!!.totalQty=binding.viewModel!!.product.value!!.quantity!!
        total=binding.viewModel!!.totalQty*binding.viewModel!!.product.value!!.unitPrice!!
        binding.textViewTotalQty.text=binding.viewModel!!.totalQty.toString()
        binding.viewModel!!.bindPrice(binding.textViewTotalamount,total)
    }
    private fun addCart(binding: FragmentProduct1688AddCartBinding,container: ViewGroup?){
        if(binding.viewModel!!.totalQty<binding.viewModel!!.product.value!!.minQty!!){
            binding.viewModel!!.showMadal(requireContext(),getString(R.string.total_quantity_must_grater_or_equal).replace("{0}",binding.viewModel!!.product.value!!.minQty!!.toString()))
            return
        }
        binding.viewModel!!.hideKeyboard(requireContext(),requireView())
        binding.viewModel!!.product.value!!.description=binding.messageToCustomer.text.toString()
        binding.viewModel!!.onAddCart(requireContext(),container!!,requireActivity())
    }
    private fun addToPurchase(binding: FragmentProduct1688AddCartBinding,container: ViewGroup?){
        if(binding.viewModel!!.totalQty<binding.viewModel!!.product.value!!.minQty!!){
            binding.viewModel!!.showMadal(requireContext(),getString(R.string.total_quantity_must_grater_or_equal).replace("{0}",binding.viewModel!!.product.value!!.minQty!!.toString()))
            return
        }
        binding.viewModel!!.hideKeyboard(requireContext(),requireView())
        binding.viewModel!!.product.value!!.description=binding.messageToCustomer.text.toString()
        binding.viewModel!!.addPurchaseCart(requireContext(),container!!)
    }
}
