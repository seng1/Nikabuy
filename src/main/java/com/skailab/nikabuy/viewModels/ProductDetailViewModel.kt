package com.skailab.nikabuy.viewModels


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skailab.nikabuy.models.PriceRange
import com.skailab.nikabuy.models.ProductDetail
import com.skailab.nikabuy.room.UserDao

class ProductDetailViewModel( db: UserDao?) : BaseViewModel(db) {
    private val _product = MutableLiveData<ProductDetail>()
    val product: LiveData<ProductDetail> get() = _product
    private val _priceRange1 = MutableLiveData<PriceRange>()
    val priceRange1: LiveData<PriceRange> get() = _priceRange1
    private val _priceRange2 = MutableLiveData<PriceRange>()
    val priceRange2: LiveData<PriceRange> get() = _priceRange2
    private val _priceRange3 = MutableLiveData<PriceRange>()
    val priceRange3: LiveData<PriceRange> get() = _priceRange3
    private val _hasPriceRange = MutableLiveData<Boolean>()
    val hasPriceRange: LiveData<Boolean> get() = _hasPriceRange

    init {
       _product.value=ProductDetail()
        _priceRange1.value=PriceRange(null,null,null,"",false)
        _priceRange1.value!!.isHasPriceRange=false
        _priceRange2.value=PriceRange(null,null,null,"",false)
        _priceRange2.value!!.isHasPriceRange=false
        _priceRange3.value=PriceRange(null,null,null,"",false)
        _priceRange3.value!!.isHasPriceRange=false
        _hasPriceRange.value=false
    }
    fun setProduct( product:ProductDetail){
        _product.value=product
        if(_product.value!=null && _product.value!!.description!=null){

            if(!_product.value!!.description!!.startsWith("<!DOCTYPE html>")){
                _product.value!!.description="<!DOCTYPE html><html> <head><meta charset=\"utf-8\" /> <title></title></head> <body><div>"+_product.value!!.description+"</div></body></html>"
            }



        }
        if(_product.value!=null && _product.value!!.priceRanges!=null && _product.value!!.priceRanges!!.count()>0){
            if(_product.value!!.priceRanges!!.count()>=1){
                _priceRange1.value=_product.value!!.priceRanges!![0]
            }
            if(_product.value!!.priceRanges!!.count()>=2){
                _priceRange2.value=_product.value!!.priceRanges!![1]
            }
            if(_product.value!!.priceRanges!!.count()>=3){
                _priceRange3.value=_product.value!!.priceRanges!![2]
            }
            _hasPriceRange.value=true
        }
        else{
            _hasPriceRange.value=false
        }

    }
}