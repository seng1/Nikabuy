package com.skailab.nikabuy.viewModels

import android.content.Context
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.skailab.nikabuy.R
import com.skailab.nikabuy.models.BuyerDashboard
import com.skailab.nikabuy.room.User
import com.skailab.nikabuy.room.UserDao
import com.skailab.nikabuy.services.UserServiceApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AccountDashboardViewModel(db: UserDao?) : BaseViewModel(db)  {
    private val _buyerDashboard = MutableLiveData<BuyerDashboard>()
    val buyerDashboard: LiveData<BuyerDashboard> get() = _buyerDashboard
    private val _hasSaleman = MutableLiveData<Boolean>()
    val hasSaleman: LiveData<Boolean> get() = _hasSaleman
    private val _salemanCode = MutableLiveData<String>()
    val salemanCode: LiveData<String> get() = _salemanCode
    init {
        _buyerDashboard.value= BuyerDashboard()
        _hasSaleman.value=true
        _salemanCode.value=""
    }
    fun  getBuyer(context:Context){
        uiScope.launch {
            try {
                showWaiting(context)
                val result = UserServiceApi.retrofitService.getBuyerDashboardAsync(userEntity.value!!.buyerId).await()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{
                    _buyerDashboard.value=result.buyerDashboard!!
                    if(_buyerDashboard.value!!.buyer!!.saleMan!= null){
                        _hasSaleman.value=true
                        _salemanCode.value=_buyerDashboard.value!!.buyer!!.saleMan!!.code
                    }
                    else{
                        _hasSaleman.value=false
                    }
                }
                hideWaiting()
            } catch (e: Exception) {
                displayException(context,e)

            }
        }
    }
    fun signOut(container:ViewGroup){
        uiScope.launch {
            logout()
            container.findNavController().navigate(R.id.accountFragment)
        }
    }
    private suspend fun logout() {
        return withContext(Dispatchers.IO) {
           database!!.clear()
        }
    }
}