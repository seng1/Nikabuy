package com.skailab.nikabuy.viewModels

import android.content.Context
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.skailab.nikabuy.R
import com.skailab.nikabuy.room.User
import com.skailab.nikabuy.room.UserDao
import com.skailab.nikabuy.services.CartServiceApi
import com.skailab.nikabuy.services.UserServiceApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ForgetPasswordViewModel( db: UserDao?) : BaseViewModel(db) {
    private val _showPhonePanel = MutableLiveData<Boolean>()
    val showPhonePanel: LiveData<Boolean> get() = _showPhonePanel
    private val _showVerificationPanel = MutableLiveData<Boolean>()
    val showVerificationPanel: LiveData<Boolean> get() = _showVerificationPanel
    private val _showPasswordPanel = MutableLiveData<Boolean>()
    val showPasswordPanel: LiveData<Boolean> get() = _showPasswordPanel
    private  var _phoneNumber:String=""
    private  var _verificationCode:String=""
    private  var _userId:String=""
    init {
        _showPhonePanel.value=true
        _showVerificationPanel.value=false
        _showPasswordPanel.value=false
    }
    fun setPhone(phoneNumber:String,context: Context){
        _phoneNumber=phoneNumber
        sendVerificationCode(context)
    }
    fun sendVerificationCode(context: Context){
        uiScope.launch {
            try {
                showWaiting(context)
                val result = UserServiceApi.retrofitService.sendVerificationCodeAsync(_phoneNumber).await()
                hideWaiting()
                if(!result.isSucess){
                    showMadal(context,result.errorText!!)
                }
                else{
                    _userId=result.userId!!
                    _verificationCode=result.verificationCode!!
                    _showPhonePanel.value=false
                    _showVerificationPanel.value=true
                }
            } catch (e: Exception) {
                displayException(context,e)
            }
        }
    }
    fun confirmVerificationCode(code:String,context: Context){
        if(code.isNullOrEmpty()){
            showMadal(context,"Verification code is require")
            return
        }
        if(code!=_verificationCode){
            showMadal(context,"Verification code doesn't correct")
            return
        }
        _showVerificationPanel.value=false
        _showPasswordPanel.value=true
    }
    fun  resetPassword(context: Context,password:String,container:ViewGroup){
        uiScope.launch {
            try {
                showWaiting(context)
                val result = UserServiceApi.retrofitService.resetPasswordAsync(_userId,password).await()
                hideWaiting()
                if(!result.isSucess){
                    showMadal(context,result.errorText!!)
                }
                else{
                    val user= User(1,
                        result.user!!.phoneNumber!!,result.user.buyer!!.id,result.user.buyer!!.code!!,
                        result.user.userName!!,result.user.id!!,productProvider,getDefaultLanguage())
                    withContext(Dispatchers.IO) {
                        database!!.insert(user)
                    }
                    container!!.findNavController().navigate(R.id.homeFragment)
                }
            } catch (e: Exception) {
                displayException(context,e)
            }
        }
    }
}