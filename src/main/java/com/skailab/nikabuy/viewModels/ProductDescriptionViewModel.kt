package com.skailab.nikabuy.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skailab.nikabuy.models.ProductDetail
import com.skailab.nikabuy.room.UserDao

class ProductDescriptionViewModel( db: UserDao?) : BaseViewModel(db) {
    private val _product = MutableLiveData<ProductDetail>()
    val product: LiveData<ProductDetail> get() = _product
    init {
        _product.value= ProductDetail()
    }
    fun setProduct( product: ProductDetail){
        _product.value=product
        if(_product.value!=null && _product.value!!.description!=null){
            if(!_product.value!!.description!!.startsWith("<!DOCTYPE html>")){
                _product.value!!.description="<!DOCTYPE html><html> <head><meta charset=\"utf-8\" /> <title></title></head> <body>"+_product.value!!.description+"</body></html>"
            }
        }
    }
}