package com.skailab.nikabuy.viewModels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skailab.nikabuy.R
import com.skailab.nikabuy.adapter.ProductAdapter
import com.skailab.nikabuy.models.Product
import com.skailab.nikabuy.models.filter.OtFilter
import com.skailab.nikabuy.room.UserDao
import com.skailab.nikabuy.room.productcache.ProductDao
import com.skailab.nikabuy.services.ProductServiceApi
import kotlinx.coroutines.launch

class ImageViewModel(db: UserDao?, productDb: ProductDao) : SearchViewModel(db,productDb) {
    val filter:OtFilter= OtFilter()
    private val _items = MutableLiveData<MutableList<Product>>()
    val items: LiveData<MutableList<Product>> get() = _items
    private var previuseIndex:Int=1
    private val _hasProduct = MutableLiveData<Boolean>()
    val hasProduct: LiveData<Boolean> get() = _hasProduct
    private val _showImageButton = MutableLiveData<Boolean>()
    val showImageButton: LiveData<Boolean> get() = _showImageButton
    private val _showWaiting = MutableLiveData<Boolean>()
    val showWaiting: LiveData<Boolean> get() = _showWaiting
    private val _showCenterWaiting = MutableLiveData<Boolean>()
    val showCenterWaiting: LiveData<Boolean> get() = _showCenterWaiting
    init {
        _items.value= mutableListOf()
        _hasProduct.value=true
        _showImageButton.value=true
        _showWaiting.value=false
        _showCenterWaiting.value=false
    }
    fun  SetImageBaseString(baseString:String,context: Context,adapter: ProductAdapter){
        filter.baseString=baseString
        filter.buyerId=userEntity.value!!.buyerId
        _showImageButton.value=false
        uiScope.launch {
            try {
                showWaiting(context)
                val result = ProductServiceApi.retrofitService.getProductImageSearchFromBaseStringAsync(filter).await()
                hideWaiting()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{
                    previuseIndex=_items.value!!.count()
                    _items.value!!.addAll(result.products)
                    filter.page+=1
                    filter.imageUrl=result.imageUrl
                    filter.baseString=""
                    adapter.notifyItemRangeInserted(previuseIndex+1,items.value!!.count())
                    _hasProduct.value= result.products.count()>0
                    if(result.products.count()>0){
                        insertProductCache(result.products)
                    }
                }
            } catch (e: Exception) {
                displayException(context,e)
            }
        }
    }
    fun onGetProducts(context: Context,adapter: ProductAdapter) {
        uiScope.launch {
            try {
                if(_items.value!!.count()>0){
                    _showWaiting.value=true
                }
                else{
                    _showCenterWaiting.value=true
                }
                val result = ProductServiceApi.retrofitService.GetOtProductsAsync(filter).await()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{
                    _items.value!!.addAll(result.products!!)
                    if(filter.page==1){
                        _hasProduct.value= result.products.count()>0
                    }
                    previuseIndex-=1
                    filter.page+=1
                    adapter.notifyItemRangeInserted(previuseIndex,result.products.count())
                    if(result.products.count()>0){
                        insertProductCache(result.products)
                    }
                }
                _showWaiting.value=false
                _showCenterWaiting.value=false
            } catch (e: Exception) {
                displayException(context,e)
            }
        }

    }
}