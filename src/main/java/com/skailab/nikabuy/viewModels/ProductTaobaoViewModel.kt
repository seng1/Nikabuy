package com.skailab.nikabuy.viewModels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skailab.nikabuy.adapter.ProductAdapter
import com.skailab.nikabuy.models.Product
import com.skailab.nikabuy.models.ProductTaobao
import com.skailab.nikabuy.models.filter.OtFilter
import com.skailab.nikabuy.room.UserDao
import com.skailab.nikabuy.services.ProductServiceApi
import kotlinx.coroutines.launch

class ProductTaobaoViewModel( db: UserDao?) : BaseViewModel(db) {
    private val _product = MutableLiveData<ProductTaobao>()
    val product: LiveData<ProductTaobao> get() = _product
    private var _suggestProducts = MutableLiveData<MutableList<Product>>()
    val suggestProducts: LiveData<MutableList<Product>> get() = _suggestProducts
    private val _showLoadingSuggestProduct = MutableLiveData<Boolean>()
    val showLoadingSuggestProduct: LiveData<Boolean> get() = _showLoadingSuggestProduct
    init {
        _suggestProducts.value= mutableListOf()
        _showLoadingSuggestProduct.value=true
    }
    fun setProduct(product:ProductTaobao){
        _product.value=product
    }
    fun getSuggestProduct(context: Context){
        if(_suggestProducts.value!!.count()==0){
            uiScope.launch {
                try {
                    var filter: OtFilter = OtFilter()
                    filter.buyerId=userEntity.value!!.buyerId
                    filter.itemId=_product.value!!.itemId!!
                    filter.provider=product.value!!.providerType!!
                    filter.categoryId=product.value!!.categoryId
                    filter.brandId=product.value!!.brandId
                    filter.vendorId=product.value!!.shopId
                    val result = ProductServiceApi.retrofitService.GetSuggestGoods(filter).await()
                    hideWaiting()
                    if (result.isSucess) {
                        _suggestProducts.value=result.products
                        _showLoadingSuggestProduct.value=false
                    }

                } catch (e: Exception) {
                    displayException(context,e)
                }
            }
        }
    }
    fun getProductDetail(context:Context,itemId:String){
        showWaiting(context)
        uiScope.launch {
            try {
                val filter:OtFilter=OtFilter()
                filter.buyerId=userEntity.value!!.buyerId
                filter.itemId=itemId
                val result = ProductServiceApi.retrofitService.getProductDetailAsync(filter).await()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{
                   _product.value=result.productTaobao
                }
                hideWaiting()
            } catch (e: Exception) {
                displayException(context,e)
            }
        }
    }
}