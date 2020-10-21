package com.skailab.nikabuy.viewModels

import android.content.Context
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.skailab.nikabuy.R
import com.skailab.nikabuy.room.User
import com.skailab.nikabuy.room.UserDao
import com.skailab.nikabuy.services.UserServiceApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel( db: UserDao?) : BaseViewModel(db) {
    var userName:String=""
    var password:String=""
    fun  login(context: Context,container:ViewGroup){
        showWaiting(context)
        uiScope.launch {
            try {
                val result = UserServiceApi.retrofitService.signInAsync(userName,password).await()
                hideWaiting()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{
                    val user= User(1,
                        result.user!!.phoneNumber!!,result.user.buyer!!.id,result.user.buyer!!.code!!,
                        result.user.userName!!,result.user.id!!,productProvider,getDefaultLanguage())
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
    fun loginWithFacebook(context: Context,container: ViewGroup,id:String,name:String){
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