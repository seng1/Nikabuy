package com.skailab.nikabuy.viewModels

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.skailab.nikabuy.R
import com.skailab.nikabuy.models.Register
import com.skailab.nikabuy.models.SaleMan
import com.skailab.nikabuy.room.User
import com.skailab.nikabuy.room.UserDao
import com.skailab.nikabuy.services.UserServiceApi
import kotlinx.coroutines.*


class RegisterViewModel(var db: UserDao?) : BaseViewModel(db) {
    private var _register = MutableLiveData<Register>()
    val register: LiveData<Register> get() = _register
    private  var _showRegisterPanel= MutableLiveData<Boolean>()
    val showRegisterPanel: LiveData<Boolean> get() = _showRegisterPanel
    private val _saleMens = MutableLiveData<MutableList<SaleMan>>()
    val saleMens: LiveData<MutableList<SaleMan>> get() = _saleMens
    init {
        _register.value= Register()
        _showRegisterPanel.value=true
    }
    fun onSendSms(context: Context) {
        showWaiting(context)
        uiScope.launch {
            try {
              val result =UserServiceApi.retrofitService.SendSmsForRegisterUserAsync(register.value!!.phone).await()
                hideWaiting()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{
                    _showRegisterPanel.value=false
                    _register.value!!.verificationCode=result.verificationCode.toString()
                    Log.v(TAG, "Payment SMS code=" + _register.value!!.verificationCode)
                }
            } catch (e: Exception) {
                hideWaiting()
                showMadal(context,e.toString())
            }
        }
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
    fun onRegister(context: Context,container: ViewGroup){
        showWaiting(context)
        uiScope.launch {
            try {
                val result =UserServiceApi.retrofitService.RegisterAsync(register.value!!).await()
                hideWaiting()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else {
                    val user= User(1,
                        result.user!!.phoneNumber!!,result.user.buyer!!.id,result.user.buyer!!.code!!,
                        result.user.userName!!,result.user.id!!,productProvider,getDefaultLanguage())
                    withContext(Dispatchers.IO) {
                        database!!.insert(user)
                    }
                    container.findNavController().navigate(R.id.homeFragment)
                }

            } catch (e: Exception) {
                hideWaiting()
                showMadal(context,e.toString())
            }
        }
    }
    fun registerWithFacebook(context: Context,container: ViewGroup,id:String,name:String){
        showWaiting(context)
        uiScope.launch {
            try {
                val result = UserServiceApi.retrofitService.registerOrLoginWithFacebookAsync(id,name).await()
                hideWaiting()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{
                    val user= User(1,
                        result.user!!.phoneNumber!!,result.user!!.buyer!!.id,result.user!!.buyer!!.code!!,
                        result.user!!.userName!!,result.user!!.id!!,productProvider,getDefaultLanguage())
                    withContext(Dispatchers.IO) {
                        database!!.insert(user)
                    }
                    container.findNavController().navigate(R.id.homeFragment)
                }
            } catch (e: Exception) {
                displayException(context,e)
            }
        }
    }
}