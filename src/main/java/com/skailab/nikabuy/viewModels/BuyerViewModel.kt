package com.skailab.nikabuy.viewModels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skailab.nikabuy.models.Buyer
import com.skailab.nikabuy.room.UserDao
import com.skailab.nikabuy.services.CartServiceApi
import com.skailab.nikabuy.services.UserServiceApi
import kotlinx.coroutines.launch

class BuyerViewModel( db: UserDao?) : BaseViewModel(db) {
    private val _buyer = MutableLiveData<Buyer>()
    val buyer: LiveData<Buyer> get() = _buyer
    init {
        _buyer.value=Buyer()
    }
    fun getBuyer(context: Context){
        uiScope.launch {
            try {
                showWaiting(context)
                val result = UserServiceApi.retrofitService.getBuyerAsync(userEntity.value!!.buyerId).await()
                hideWaiting()
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
    fun updateBuyer(context: Context){
        if(_buyer.value!!.name.isNullOrEmpty()){
            showMadal(context,"Your name is require")
            return
        }
        if(_buyer.value!!.telephone.isNullOrEmpty()){
            showMadal(context,"Your phone is require")
            return
        }
        if(!_buyer.value!!.email.isNullOrEmpty()){
            if(!isValidEmail(_buyer.value!!.email!!)){
                showMadal(context,"Your email is not correct format")
                return
            }
        }
        uiScope.launch {
            try {
                showWaiting(context)
                val result = UserServiceApi.retrofitService.updateBuyerAsync(_buyer.value!!).await()
                hideWaiting()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{
                    showMadal(context,"Your information update successfully")
                }
            } catch (e: Exception) {
                displayException(context,e)
            }
        }
    }
}