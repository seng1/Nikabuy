package com.skailab.nikabuy.viewModels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skailab.nikabuy.models.Product
import com.skailab.nikabuy.models.Product1688
import com.skailab.nikabuy.models.ProductDetail
import com.skailab.nikabuy.models.ProductTaobao
import com.skailab.nikabuy.models.filter.OtFilter
import com.skailab.nikabuy.room.UserDao
import com.skailab.nikabuy.room.productcache.ProductDao
import com.skailab.nikabuy.services.ProductServiceApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class SearchViewModel(db: UserDao?,val productDb:ProductDao) : BaseViewModel(db) {
    private val _productDetail = MutableLiveData<ProductDetail>()
    val productDetail: LiveData<ProductDetail> get() = _productDetail
    private  val filter:OtFilter=OtFilter()
    protected val _product1688 = MutableLiveData<Product1688>()
    val product1688: LiveData<Product1688> get() = _product1688
    protected val _productTaobao = MutableLiveData<ProductTaobao>()
    val productTaobao: LiveData<ProductTaobao> get() = _productTaobao
    fun  setProduct(product:ProductDetail?){
        _productDetail.value=product
    }
    fun getProductDetail(context:Context,itemId:String){
        filter.buyerId=userEntity.value!!.buyerId
        showWaiting(context)
        uiScope.launch {
            try {
                filter.itemId=itemId
                val result = ProductServiceApi.retrofitService.getProductDetailAsync(filter).await()
               if(!result.isSucess){
                   showMadal(context,result.errorText)
               }
              else{
                  if(result.product1688!=null){
                      _product1688.value=result.product1688
                  }
                   else{
                      _productTaobao.value=result.productTaobao!!
                  }
               }
                hideWaiting()
            } catch (e: Exception) {
                displayException(context,e)
            }
        }
    }
    fun insertProductCache(products:List<Product>) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
               products.forEach {
                   var data=com.skailab.nikabuy.room.productcache.Product(0,
                       it.itemId,
                       it.title,
                       it.quantity,
                       it.unitPrice,
                       it.unitPriceInChn,
                       it.sourceUrl,
                       it.imageUrl,
                       it.shopId,
                       it.shopUrl,
                       it.originalTitle,
                       it.discountPriceRangetext,
                       it.orginalPriceRangetext,
                       System.currentTimeMillis(),
                       it.quantitySoldText)
                       productDb.insert(data)
               }
            }
        }
    }
    fun  productToNull(){
        _product1688.value=null
        _productTaobao.value=null
    }
}