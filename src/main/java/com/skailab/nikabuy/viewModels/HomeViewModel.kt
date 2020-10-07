package com.skailab.nikabuy.viewModels

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skailab.nikabuy.adapter.ProductAdapter
import com.skailab.nikabuy.models.Buyer
import com.skailab.nikabuy.models.Product
import com.skailab.nikabuy.models.SaleMan
import com.skailab.nikabuy.models.filter.ProductFilter
import com.skailab.nikabuy.room.UserDao
import com.skailab.nikabuy.room.productcache.ProductDao
import com.skailab.nikabuy.services.ProductServiceApi
import com.skailab.nikabuy.services.UserServiceApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomeViewModel(db: UserDao?, productDb: ProductDao) : SearchViewModel(db, productDb)  {
    private var filter:ProductFilter= ProductFilter()
    private val _items = MutableLiveData<MutableList<Product>>()
    val items: LiveData<MutableList<Product>> get() = _items
    private val _showAllProductCache = MutableLiveData<Boolean>()
    val showAllProductCache: LiveData<Boolean> get() = _showAllProductCache
    private val _showWaiting = MutableLiveData<Boolean>()
    val showWaiting: LiveData<Boolean> get() = _showWaiting
    private val _showCenterWaiting = MutableLiveData<Boolean>()
    val showCenterWaiting: LiveData<Boolean> get() = _showCenterWaiting
    private var previuseIndex:Int=1

    private val _buyer = MutableLiveData<Buyer>()
    val buyer: LiveData<Buyer> get() = _buyer



   fun onInit(context: Context,adapter: ProductAdapter){
       _items.value= mutableListOf()
        filter.buyerId=0
       if(userEntity.value!=null && userEntity.value!!.buyerId!= null ){
           filter.buyerId=  userEntity.value!!.buyerId
       }
       _showWaiting.value=false
       _showAllProductCache.value=false
       _showCenterWaiting.value=false
       getProductFromCache(context,adapter,true)
    }
    fun getBuyer(context: Context){
        uiScope.launch {
            try {
                val result = UserServiceApi.retrofitService.getBuyerAsync(userEntity.value!!.buyerId).await()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{
                   _buyer.value=result.buyer!!
                }
            } catch (e: Exception) {
                displayException(context,e)
            }
        }
    }


    private fun getProductFromCache(context:Context, adapter: ProductAdapter,showWaitingDialog:Boolean){
        uiScope.launch {
            try {
                if(showWaitingDialog){
                    showWaiting(context)
                }
                else{
                    _showCenterWaiting.value=true
                }

                withContext(Dispatchers.IO) {
                    var skip:Int=0
                    if(filter.page>1){
                        skip=(filter.page-1)*filter.pageSize
                    }
                    val products=productDb.loadAllProductsByPage(filter.pageSize,skip)
                    Handler(Looper.getMainLooper()).post(Runnable {
                        if(showWaitingDialog){
                            hideWaiting()
                        }
                        else{
                            _showWaiting.value=false
                        }
                        if(products==null || products.size==0){
                            _showAllProductCache.value=true
                            filter.page=1
                            onGetProducts(context,adapter)
                        }
                        else{
                            previuseIndex = _items.value!!.count()
                            products.forEach {
                                val product=Product()
                                product.itemId=it!!.itemId
                                product.discountPriceRangetext= it.discountPriceRangetext
                                product.imageUrl=it.imageUrl
                                product.orginalPriceRangetext=it.orginalPriceRangetext
                                product.quantity=it.quantity
                                product.quantitySoldText=it.quantitySoldText
                                product.originalTitle=it.originalTitle
                                product.shopId=it.shopId
                                product.shopUrl=it.shopUrl
                                product.sourceUrl=it.sourceUrl
                                product.title=it.title
                                product.unitPrice=it.unitPrice
                                product.unitPriceInChn=it.unitPriceInChn
                                _items.value!!.add(product)
                                filter.page += 1
                                adapter.notifyItemRangeInserted(previuseIndex + 1, items.value!!.count())

                            }
                        }
                    })
                }
            } catch (e: Exception) {
                displayException(context, e)
            }
        }
    }
   fun onGetProducts(context: Context,adapter: ProductAdapter) {
      if(!_showAllProductCache.value!!){
          getProductFromCache(context,adapter,false)
      }
       else{
          onGetProductsFromLive(context,adapter)
      }
   }
    fun onGetProductsFromLive(context: Context,adapter: ProductAdapter) {
        if(_items.value!!.count()>0){
            _showWaiting.value=true
        }
        else{
            _showCenterWaiting.value=true
        }
        uiScope.launch {
            try {
                val result = ProductServiceApi.retrofitService.getProductListingAsync(filter).await()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{
                    previuseIndex=_items.value!!.count()
                    _items.value!!.addAll(result.products!!)
                    previuseIndex-=1
                    filter.page+=1
                    adapter.notifyItemRangeInserted(previuseIndex,result.products!!.count())
                }
                _showWaiting.value=false
                _showCenterWaiting.value=false
            } catch (e: Exception) {
                displayException(context,e)
            }
        }

    }
}