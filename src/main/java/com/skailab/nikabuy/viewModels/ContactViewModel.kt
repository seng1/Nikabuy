package com.skailab.nikabuy.viewModels


import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skailab.nikabuy.adapter.ContactAdapter
import com.skailab.nikabuy.models.Contact
import com.skailab.nikabuy.models.filter.Filter
import com.skailab.nikabuy.room.UserDao
import com.skailab.nikabuy.services.ContactServiceApi
import kotlinx.coroutines.launch

class ContactViewModel( db: UserDao?) : BaseViewModel(db) {
    private val _contacts = MutableLiveData<MutableList<Contact>>()
    val contacts: LiveData<MutableList<Contact>> get() = _contacts
    private val _hasContact = MutableLiveData<Boolean>()
    val hasContact: LiveData<Boolean> get() = _hasContact
    init {
        _contacts.value= mutableListOf()
        _hasContact.value=true
    }
    fun getContacts(context: Context,adapter:ContactAdapter){
        val filter = Filter()
        filter.buyerId= userEntity.value!!.buyerId
        uiScope.launch {
            try {
                showWaiting(context)
                val result = ContactServiceApi.retrofitService.getsAsync(filter).await()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{
                  _contacts.value=result.contacts!!
                    _hasContact.value= result.contacts.count()>0
                  adapter.notifyDataSetChanged()
                }
                hideWaiting()
            } catch (e: Exception) {
                displayException(context,e)

            }
        }
    }
    fun  delete(
        context: Context,
        adapter: ContactAdapter,
        id: Int
    ){
        uiScope.launch {
            try {
                showWaiting(context)
                val result = ContactServiceApi.retrofitService.deleteAsync(id).await()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{
                    getContacts(context,adapter)
                }
                hideWaiting()
            } catch (e: Exception) {
                displayException(context,e)

            }
        }
    }
}