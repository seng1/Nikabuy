package com.skailab.nikabuy.viewModels

import android.app.Activity
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skailab.nikabuy.models.SaleMan
import com.skailab.nikabuy.room.UserDao
import com.skailab.nikabuy.services.UserServiceApi
import kotlinx.coroutines.launch

class UpdateSaleManViewModel(db: UserDao? ) : BaseViewModel(db) {
    private val _saleMens = MutableLiveData<MutableList<SaleMan>>()
    val saleMens: LiveData<MutableList<SaleMan>> get() = _saleMens
    init {
    }
    fun getSaleMens(context: Context){
        uiScope.launch {
            try {
                showWaiting(context)
                val result = UserServiceApi.retrofitService.getSaleMansAsync().await()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{
                    _saleMens.value=result.saleMens
                }
                hideWaiting()
            } catch (e: Exception) {
                displayException(context,e)
            }
        }
    }
    fun updateSaleMan(context: Context,saleManCode:String,activtity: Activity){
        uiScope.launch {
            try {
                showWaiting(context)
                val result = UserServiceApi.retrofitService.updateSaleManAsync(userEntity.value!!.buyerId,saleManCode).await()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{
                   activtity.onBackPressed()
                }
                hideWaiting()
            } catch (e: Exception) {
                displayException(context,e)

            }
        }
    }
}