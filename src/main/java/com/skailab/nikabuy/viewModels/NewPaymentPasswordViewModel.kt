package com.skailab.nikabuy.viewModels

import android.app.Activity
import android.content.Context
import android.view.ViewGroup
import com.skailab.nikabuy.room.UserDao
import com.skailab.nikabuy.services.PaymentServiceApi
import kotlinx.coroutines.launch

class NewPaymentPasswordViewModel( db: UserDao?) : BaseViewModel(db) {
    fun  Save(context: Context,container:ViewGroup,password:String,activtity: Activity)
    {
        uiScope.launch {
            try {
                showWaiting(context)
                val result = PaymentServiceApi.retrofitService.setPaymentPasswordAsync(userEntity.value!!.buyerId,password).await()
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