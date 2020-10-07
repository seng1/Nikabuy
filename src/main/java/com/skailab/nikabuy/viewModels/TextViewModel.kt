package com.skailab.nikabuy.viewModels

import android.content.Context
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.skailab.nikabuy.adapter.ProductAdapter
import com.skailab.nikabuy.models.Product
import com.skailab.nikabuy.models.ProductDetail
import com.skailab.nikabuy.models.filter.OtFilter
import com.skailab.nikabuy.room.UserDao
import com.skailab.nikabuy.room.productcache.ProductDao
import com.skailab.nikabuy.services.ProductServiceApi
import kotlinx.coroutines.launch

class TextViewModel( db: UserDao?, productDb: ProductDao) : SearchViewModel(db,productDb) {
    private val _items = MutableLiveData<MutableList<Product>>()
    val items: LiveData<MutableList<Product>> get() = _items
    val filter: OtFilter = OtFilter()
    private var previuseIndex:Int=1
    private val _hasProduct = MutableLiveData<Boolean>()
    val hasProduct: LiveData<Boolean> get() = _hasProduct
    private val _showWaiting = MutableLiveData<Boolean>()
    val showWaiting: LiveData<Boolean> get() = _showWaiting
    private val _showCenterWaiting = MutableLiveData<Boolean>()
    val showCenterWaiting: LiveData<Boolean> get() = _showCenterWaiting
    init {
        _items.value= mutableListOf()
        _hasProduct.value=true
        _showWaiting.value=false
        _showCenterWaiting.value=false
    }
    fun onGetProducts(context: Context, adapter: ProductAdapter) {
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
                    if(result.products!=null){
                        previuseIndex=_items.value!!.count()
                        _items.value!!.addAll(result.products)
                        adapter.notifyItemRangeInserted(previuseIndex,result.products.count())
                        filter.page+=1
                        if(result.products.count()>0){
                            insertProductCache(result.products)
                        }
                    }
                }
                _showWaiting.value=false
            } catch (e: Exception) {
                displayException(context,e)

            }
        }
    }
    fun  setSearchText(searchText:String,context: Context,adapter: ProductAdapter){
        filter.itemTitle=searchText
        filter.buyerId=userEntity.value!!.buyerId
        filter.page=1
        _items.value!!.clear()
        adapter.notifyDataSetChanged()
        uiScope.launch {
            try {
                showWaiting(context)
                val result = ProductServiceApi.retrofitService.getProductsTextSearchAsync(filter).await()
                hideWaiting()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{
                    if(result.products!=null){
                        previuseIndex=_items.value!!.count()
                        _items.value!!.addAll(result.products)
                        filter.page+=1
                        filter.itemTitle=result.translateText!!
                        filter.baseString=""
                        adapter.notifyItemRangeInserted(previuseIndex+1,items.value!!.count())
                        if(result.products.count()>0){
                            insertProductCache(result.products)
                        }
                        _hasProduct.value= result.products.count()>0
                    }
                    else if(result.product1688!= null){
                        _product1688.value=result.product1688
                    }
                    else if(result.productTaobao!=null){
                        _productTaobao.value=result.productTaobao
                    }

                }
            } catch (e: Exception) {
                displayException(context,e)
            }
        }
    }

}