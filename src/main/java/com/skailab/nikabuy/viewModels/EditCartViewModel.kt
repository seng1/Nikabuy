package com.skailab.nikabuy.viewModels

import android.content.Context
import android.graphics.Bitmap
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.skailab.nikabuy.R
import com.skailab.nikabuy.models.Cart
import com.skailab.nikabuy.room.UserDao
import com.skailab.nikabuy.services.CartServiceApi
import kotlinx.coroutines.launch

class EditCartViewModel( db: UserDao?) : BaseViewModel(db) {
    private val _cart = MutableLiveData<Cart>()
    val cart: LiveData<Cart> get() = _cart
    private val _hasImage = MutableLiveData<Boolean>()
    val hasImage: LiveData<Boolean> get() = _hasImage
    init {
        _hasImage.value=false
    }
    fun setCart(cart:Cart){
        _cart.value=cart
        _hasImage.value=!cart.userImageUrl.isNullOrEmpty()
    }
    fun setImage(bm: Bitmap){
        _cart.value!!.userImageUrl=getBase64(bm)
        _hasImage.value=true
    }
    fun  clearImage(){
        _cart.value!!.userImageUrl=""
        _hasImage.value=false
    }
    fun save(context: Context,container: ViewGroup){
        if(_cart.value!!.quantity==null || _cart.value!!.quantity!!<=0){
            showMadal(context,"Quantity is require")
            return
        }
        uiScope.launch {
            try {
                showWaiting(context)
                val result = CartServiceApi.retrofitService.updateAsync(_cart.value!!).await()
                hideWaiting()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{
                    container.findNavController().navigate(R.id.cardFragment)
                }
            } catch (e: Exception) {
                displayException(context,e)
            }
        }
    }
}