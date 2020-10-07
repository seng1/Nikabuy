package com.skailab.nikabuy.viewModels

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import com.skailab.nikabuy.B2cActivity
import com.skailab.nikabuy.MainActivity
import com.skailab.nikabuy.room.User
import com.skailab.nikabuy.room.UserDao
import com.skailab.nikabuy.services.UserServiceApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class B2CViewModel( db: UserDao?) : BaseViewModel(db) {
    fun login(activity: Activity,userId:String){
        uiScope.launch {
            try {
                var result = UserServiceApi.retrofitService.getAzureB2CUserAsync(userId).await()
                hideWaiting()
                if(!result.isSucess){
                    Toast.makeText(activity.application,result.errorText,Toast.LENGTH_LONG).show()
                }
                else{
                    val user= User(1,
                        result.user.phoneNumber!!,result.user.buyer!!.id,result.user.buyer!!.code!!,
                        result.user.userName!!,result.user.id!!,productProvider,getDefaultLanguage())
                    withContext(Dispatchers.IO) {
                        database!!.insert(user)
                    }
                    val intent = Intent(activity, MainActivity::class.java).apply {
                    }
                    activity.startActivity(intent)
                }
            } catch (e: Exception) {
                Toast.makeText(activity.application,e.toString(),Toast.LENGTH_LONG).show()
            }
        }
    }
}