package com.skailab.nikabuy.viewModels

import android.content.Context
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.skailab.nikabuy.R
import com.skailab.nikabuy.room.UserDao
import com.skailab.nikabuy.services.PaymentServiceApi
import kotlinx.coroutines.launch

class PaymentPasswordViewModel( db: UserDao?) : BaseViewModel(db) {
    fun  Save(context: Context,container:ViewGroup,password:String)
    {
        uiScope.launch {
            try {
                showWaiting(context)
                val result = PaymentServiceApi.retrofitService.setPaymentPasswordAsync(userEntity.value!!.buyerId,password).await()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{
                   container.findNavController().navigate(R.id.accountDashboardFragment)
                }
                hideWaiting()
            } catch (e: Exception) {
                displayException(context,e)

            }
        }
    }
}