package com.skailab.nikabuy.viewModels

import android.app.Activity
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skailab.nikabuy.models.Contact
import com.skailab.nikabuy.models.Region
import com.skailab.nikabuy.models.api.RegionApiResult
import com.skailab.nikabuy.models.filter.Filter
import com.skailab.nikabuy.room.UserDao
import com.skailab.nikabuy.services.ContactServiceApi
import kotlinx.coroutines.launch


class UpdateContactViewModel( db: UserDao?) : BaseViewModel(db) {
    private val _contact = MutableLiveData<Contact>()
    val contact: LiveData<Contact> get() = _contact
    private val _region = MutableLiveData<RegionApiResult>()
    val region: LiveData<RegionApiResult> get() = _region
    init {
        _contact.value= Contact()
        _contact.value!!.country=Region()
        _contact.value!!.district=Region()
        _contact.value!!.province= Region()
    }
    fun setContact(con:Contact){
        _contact.value=con
    }
    fun getRegions(context: Context){
        uiScope.launch {
            try {
                showWaiting(context)
                val filter:Filter= Filter()
                filter.buyerId= userEntity.value!!.buyerId
                val result = ContactServiceApi.retrofitService.getRegionsAsync(filter).await()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{
                    _region.value=result
                }
                hideWaiting()
            } catch (e: Exception) {
                displayException(context,e)

            }
        }
    }
    fun save(context: Context,activtity: Activity){
        uiScope.launch {
            try {
                showWaiting(context)
                val result = ContactServiceApi.retrofitService.updateContactAsync(userEntity.value!!.buyerId,_contact.value!!).await()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{
                    activtity.onBackPressed()
                }
                hideWaiting()
            } catch (e: Exception) {
                displayException(context,e)

            }
        }
    }
}