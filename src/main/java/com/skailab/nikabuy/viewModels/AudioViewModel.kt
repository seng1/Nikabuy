package com.skailab.nikabuy.viewModels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skailab.nikabuy.R
import com.skailab.nikabuy.adapter.ProductAdapter
import com.skailab.nikabuy.models.Product
import com.skailab.nikabuy.models.filter.AudioFilter
import com.skailab.nikabuy.room.UserDao
import com.skailab.nikabuy.room.productcache.ProductDao
import com.skailab.nikabuy.services.ProductServiceApi
import kotlinx.coroutines.launch

class AudioViewModel( db: UserDao?, productDb: ProductDao) : SearchViewModel(db,productDb) {
    val filter: AudioFilter = AudioFilter()
    private val _items = MutableLiveData<MutableList<Product>>()
    val items: LiveData<MutableList<Product>> get() = _items
    private var previuseIndex:Int=1
    val  _showStartRecordButton=MutableLiveData<Boolean>()
    val showStartRecordButton:LiveData<Boolean> get()=_showStartRecordButton
    val  _showStopRecordButton=MutableLiveData<Boolean>()
    val showStopRecordButton:LiveData<Boolean> get()=_showStopRecordButton
    private val _hasProduct = MutableLiveData<Boolean>()
    val hasProduct: LiveData<Boolean> get() = _hasProduct
    private val _showWaiting = MutableLiveData<Boolean>()
    val showWaiting: LiveData<Boolean> get() = _showWaiting
    private val _showCenterWaiting = MutableLiveData<Boolean>()
    val showCenterWaiting: LiveData<Boolean> get() = _showCenterWaiting
    init {
        _items.value= mutableListOf()
        _showStartRecordButton.value=true
        _showStopRecordButton.value=false
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
                    if(filter.page==1){
                        _hasProduct.value= result.products!!.count()>0
                    }
                    filter.page+=1
                    previuseIndex=_items.value!!.count()
                    if(result.products!!.count()>0){
                        _items.value!!.addAll(result.products)

                        adapter.notifyItemRangeInserted(previuseIndex,result.products.count())
                        if(result.products.count()>0){
                            insertProductCache(result.products)
                        }
                    }
                }
                _showWaiting.value=false
                _showCenterWaiting.value=false
            } catch (e: Exception) {
                displayException(context,e)

            }
        }

    }
    fun  SetImageBaseString(baseString:String,context: Context,adapter: ProductAdapter){
        filter.baseString=baseString
        filter.buyerId=userEntity.value!!.buyerId
        uiScope.launch {
            try {
                showWaiting(context,context.getString(R.string.search_by_audio_slow))
                val result = ProductServiceApi.retrofitService.getProductAudioSearchFromBaseStringStringAsync(filter).await()
                hideWaiting()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{
                    filter.page+=1
                    filter.itemTitle=result.text
                    filter.baseString=""
                    _hasProduct.value= result.products.count()>0
                    if(_hasProduct.value!!){
                        previuseIndex=_items.value!!.count()
                        _items.value!!.addAll(result.products)
                        adapter.notifyItemRangeInserted(previuseIndex+1,items.value!!.count())
                        if(result.products.count()>0){
                            insertProductCache(result.products)
                        }
                    }
                }
            } catch (e: Exception) {
                displayException(context,e)
                _showStartRecordButton.value=true
            }
        }
    }
}