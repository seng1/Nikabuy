package com.skailab.nikabuy.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skailab.nikabuy.adapter.ProductSkuItemAdapter
import com.skailab.nikabuy.models.ProductSkuItem
import kotlinx.android.synthetic.main.view_product_sku_item.view.*

class ProductSkuItemViewModel(private val productSkuItem:ProductSkuItem) : ViewModel() {
    private val _product = MutableLiveData<ProductSkuItem>()
    val product: LiveData<ProductSkuItem> get() = _product
    private val _price = MutableLiveData<String>()
    val price: LiveData<String> get() = _price
   private  var holder: ProductSkuItemAdapter.ProductSkuItemViewHolder?= null
    init {
        _product.value= productSkuItem
        _price.value=productSkuItem.price.toString() +" $"
    }
   fun setPrice( price:Double){
        _product.value!!.price=price
       _price.value=price.toString()+" $"
       if(holder!=null){
           holder!!.itemView.price.setText(_price.value.toString())
       }
    }
    fun  setHolder(holder: ProductSkuItemAdapter.ProductSkuItemViewHolder){
        this.holder=holder
    }

}