package com.skailab.nikabuy.viewModels

import android.content.Context
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.skailab.nikabuy.R
import com.skailab.nikabuy.room.User
import com.skailab.nikabuy.room.UserDao
import com.skailab.nikabuy.services.UserServiceApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AccountViewModel( db: UserDao?) : BaseViewModel(db) {
    val showLoginOldAccount: LiveData<Boolean> get() = _showLoginOldAccount
    private val _showLoginOldAccount = MutableLiveData<Boolean>()
    init {
        _showLoginOldAccount.value=false
    }
    fun loginWithFacebook(context: Context, container: ViewGroup, id:String, name:String){
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
    fun checkAllAccountLink(context: Context){
        showWaiting(context)
        uiScope.launch {
            try {
                val result = UserServiceApi.retrofitService.checkAllAccountLinkAsync().await()
                hideWaiting()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{
                    _showLoginOldAccount.value=!result.isAllLink
                }
            } catch (e: Exception) {
                displayException(context,e)
            }
        }
    }
}




