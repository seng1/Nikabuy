package com.skailab.nikabuy.viewModels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.skailab.nikabuy.R
import com.skailab.nikabuy.adapter.AccountLinkAdapter
import com.skailab.nikabuy.models.User
import com.skailab.nikabuy.room.UserDao
import com.skailab.nikabuy.services.UserServiceApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AccountLinkViewModel( db: UserDao?) : BaseViewModel(db) {
    private val _showMobileLinkButton = MutableLiveData<Boolean>()
    val showMobileLinkButton: LiveData<Boolean> get() = _showMobileLinkButton
    val showFacebookLinkButton: LiveData<Boolean> get() = _showFacebookLinkButton
    private val _showFacebookLinkButton = MutableLiveData<Boolean>()
    private val _users = MutableLiveData<MutableList<User>>()
    val users: LiveData<MutableList<User>> get() = _users
    init {
        _showMobileLinkButton.value=false
        _showFacebookLinkButton.value=false
        _users.value= mutableListOf()
    }
    fun getAccounts(context: Context,adapter: AccountLinkAdapter){
        uiScope.launch {
            try {
                showWaiting(context)
                val result = UserServiceApi.retrofitService.getAccountLinksAsync(userEntity.value!!.buyerId).await()
                hideWaiting()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{
                    _showMobileLinkButton.value=!result.isMobileAdded
                    _showFacebookLinkButton.value=!result.isFacebookAdded
                    _users.value=result.users
                    adapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                displayException(context,e)
            }
        }
    }
    fun  linkFacebookAccount(id:String,name:String,context: Context,adapter: AccountLinkAdapter){
        uiScope.launch {
            try {
                showWaiting(context)
                val result = UserServiceApi.retrofitService.linkFacebookAccount(userEntity.value!!.buyerId,id,name).await()
                hideWaiting()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{
                    getAccounts(context,adapter)
                }
            } catch (e: Exception) {
                displayException(context,e)
            }
        }
    }
}