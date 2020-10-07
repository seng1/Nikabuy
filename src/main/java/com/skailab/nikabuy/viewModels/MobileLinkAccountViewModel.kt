package com.skailab.nikabuy.viewModels


import android.content.Context
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.skailab.nikabuy.R
import com.skailab.nikabuy.models.Register
import com.skailab.nikabuy.room.UserDao
import com.skailab.nikabuy.services.UserServiceApi
import kotlinx.coroutines.launch

class MobileLinkAccountViewModel( db: UserDao?) : BaseViewModel(db) {
    private  var _showRegisterPanel= MutableLiveData<Boolean>()
    val showRegisterPanel: LiveData<Boolean> get() = _showRegisterPanel
    private var _phoneNumber:String=""
    private var _password:String=""
    var _verificationCode:String=""
    init {
        _showRegisterPanel.value=true
    }
    fun sendSms(context: Context,phoneNumber:String,password:String){
        _phoneNumber=phoneNumber
        _password=password
        sendVerification(context)

    }
    fun onBind(context: Context,container:ViewGroup){
        showWaiting(context)
        uiScope.launch {
            try {
                val register=Register()
                register.phone=_phoneNumber
                register.password=_password
                var result = UserServiceApi.retrofitService.linkMobileAccountAsync(userEntity.value!!.buyerId,register).await()
                hideWaiting()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{
                  container.findNavController().navigate(R.id.accountLinkFragment)
                }
            } catch (e: Exception) {
                displayException(context,e)
            }
        }
    }
    fun sendVerification(context: Context){
        showWaiting(context)
        uiScope.launch {
            try {
                var result = UserServiceApi.retrofitService.SendSmsForRegisterUserAsync(_phoneNumber).await()
                hideWaiting()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{
                    _showRegisterPanel.value=false
                    _verificationCode=result.verificationCode.toString()
                }
            } catch (e: Exception) {
                displayException(context,e)
            }
        }
    }
}