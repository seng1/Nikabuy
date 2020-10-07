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

class ProductScoreViewModel( db: UserDao?, productDb: ProductDao) : SearchViewModel(db,productDb){
    private val _product = MutableLiveData<ProductDetail>()
    private  var filter:OtFilter= OtFilter()
    val product: LiveData<ProductDetail> get() = _product
    private val _items = MutableLiveData<MutableList<Product>>()
    val items: LiveData<MutableList<Product>> get() = _items
    private val _showDetail = MutableLiveData<Boolean>()
    val showDetail: LiveData<Boolean> get() = _showDetail
    private var previuseIndex:Int=1
    init {
        _product.value= ProductDetail()
        _items.value= mutableListOf()
        _showDetail.value=true
    }
    fun  setShowDetail(isShowDetail:Boolean){
        _showDetail.value=isShowDetail
    }
    fun getInitProducts(context: Context, adapter: ProductAdapter){
        if(_product.value!=null){
            filter.buyerId=userEntity.value!!.buyerId
            filter.vendorId=_product.value!!.vendorInfo!!.id
            filter.provider=_product.value!!.providerType!!
            uiScope.launch {
                try {
                    val product=Product()
                    product.isLoading=true
                    product.isVisible=true
                    product.title="Hi"
                    _items.value!!.add((product))
                    adapter.notifyDataSetChanged()
                    val result = ProductServiceApi.retrofitService.GetOtProductsAsync(filter).await()
                    if(!result.isSucess){
                        showMadal(context,result.errorText)
                    }
                    else{
                        _items.value!!.clear()
                        if(result.products!!.count()>0) {
                            _items.value!!.addAll(result.products)
                        }
                        filter.page+=1
                        adapter.notifyDataSetChanged()
                        if(result.products.count()>0){
                            insertProductCache(result.products)
                        }
                    }
                } catch (e: Exception) {
                    displayException(context,e)

                }
            }
        }
    }
    fun onGetProducts(context: Context, adapter: ProductAdapter) {
        if(_items.value!!.count()>0){
            val lastProduct =_items.value!![_items.value!!.count()-1]
            if(lastProduct.isLoading!=null && lastProduct.isLoading!!){
                return
            }
        }
        uiScope.launch {
            try {
                val product=Product()
                product.isLoading=true
                product.isVisible=true
                product.title="Hi"
                _items.value!!.add((product))
                adapter.notifyItemInserted( _items.value!!.count()-1)
                val result = ProductServiceApi.retrofitService.GetOtProductsAsync(filter).await()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{
                    _items.value!!.remove(_items.value!![_items.value!!.count()-1])
                    previuseIndex=_items.value!!.count()
                    adapter.notifyItemRemoved(previuseIndex)
                    if(result.products!!.count()>0){
                        _items.value!!.addAll(result.products)
                        previuseIndex-=1
                        filter.page+=1
                        adapter.notifyItemRangeInserted(previuseIndex,result.products.count())
                    }
                }
            } catch (e: Exception) {
                displayException(context,e)

            }
        }

    }
    fun setProductFromAguement(product: ProductDetail){
        _product.value=product
        if(_product.value!=null && _product.value!!.description!=null){
            if(!_product.value!!.description!!.startsWith("<!DOCTYPE html>")){
                _product.value!!.description="<!DOCTYPE html><html> <head><meta charset=\"utf-8\" /> <title></title></head> <body>"+_product.value!!.description+"</body></html>"
            }

        }
    }
}