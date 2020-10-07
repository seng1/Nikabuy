package com.skailab.nikabuy.viewModels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skailab.nikabuy.adapter.ProductAdapter
import com.skailab.nikabuy.models.Product
import com.skailab.nikabuy.models.ProductDetail
import com.skailab.nikabuy.models.filter.OtFilter
import com.skailab.nikabuy.room.UserDao
import com.skailab.nikabuy.room.productcache.ProductDao
import com.skailab.nikabuy.services.ProductServiceApi
import kotlinx.coroutines.launch

class RecommendViewModel(db: UserDao?, productDb: ProductDao) : SearchViewModel(db,productDb) {
    private val _product = MutableLiveData<ProductDetail>()
    private  var filter: OtFilter = OtFilter()
    val product: LiveData<ProductDetail> get() = _product
    private val _items = MutableLiveData<MutableList<Product>>()
    val items: LiveData<MutableList<Product>> get() = _items
    private val _showWaiting = MutableLiveData<Boolean>()
    val showWaiting: LiveData<Boolean> get() = _showWaiting
    private var previuseIndex:Int=1
    init {
        _product.value= ProductDetail()
        _items.value= mutableListOf()
        _showWaiting.value=false
    }
    fun setProductFromAgument( product: ProductDetail){
        _product.value=product
    }
    fun getInitProducts(context: Context, adapter: ProductAdapter){
        if(_product.value!=null){
            filter.buyerId=userEntity.value!!.buyerId
            filter.brandId=_product.value!!.brandId
            filter.categoryId=_product.value!!.categoryId
            filter.provider=_product.value!!.providerType!!
            showWaiting(context)
            uiScope.launch {
                try {

                    val result = ProductServiceApi.retrofitService.GetOtProductsAsync(filter).await()
                    if(!result.isSucess){
                        showMadal(context,result.errorText)
                    }
                    else{
                        _items.value!!.addAll(result.products!!)
                        filter.page+=1
                        adapter.notifyDataSetChanged()
                        if(result.products.count()>0){
                            insertProductCache(result.products)
                        }
                    }
                    hideWaiting()

                } catch (e: Exception) {
                    displayException(context,e)

                }
            }
        }
    }
    fun onGetProducts(context: Context, adapter: ProductAdapter) {
        uiScope.launch {
            try {
                _showWaiting.value=true
                val result = ProductServiceApi.retrofitService.GetOtProductsAsync(filter).await()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{
                    previuseIndex=_items.value!!.count()
                    filter.page+=1
                    adapter.notifyItemRangeInserted(previuseIndex,result.products!!.count())
                    if(result.products.count()>0){
                        insertProductCache(result.products)
                    }
                }
                _showWaiting.value=false
            } catch (e: Exception) {
                displayException(context,e)

            }
        }


    }
}