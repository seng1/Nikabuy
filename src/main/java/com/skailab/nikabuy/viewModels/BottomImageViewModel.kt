package com.skailab.nikabuy.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skailab.nikabuy.models.PriceRange
import com.skailab.nikabuy.models.ProductDetail
import com.skailab.nikabuy.room.UserDao

class BottomImageViewModel( db: UserDao?) : BaseViewModel(db) {
    private val _imageUrl = MutableLiveData<String>()
    val imageUrl: LiveData<String> get() = _imageUrl
    private val _description = MutableLiveData<String>()
    val description: LiveData<String> get() = _description

    init {

    }
    fun setImage( imageUrl: String,description:String){
        _description.value=description
       _imageUrl.value=imageUrl
    }
}