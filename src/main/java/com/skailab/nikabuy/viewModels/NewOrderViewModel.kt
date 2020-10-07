package com.skailab.nikabuy.viewModels

import android.content.Context
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.skailab.nikabuy.R
import com.skailab.nikabuy.models.Contact
import com.skailab.nikabuy.models.OrderSubmit
import com.skailab.nikabuy.models.ProductCartParcelize
import com.skailab.nikabuy.models.filter.Filter
import com.skailab.nikabuy.room.UserDao
import com.skailab.nikabuy.services.ContactServiceApi
import com.skailab.nikabuy.services.OrderServiceApi
import kotlinx.coroutines.launch

class NewOrderViewModel( db: UserDao?) : BaseViewModel(db) {
    private val _order = MutableLiveData<OrderSubmit>()
    private val _hasAddress = MutableLiveData<Boolean>()
    val hasAddress: LiveData<Boolean> get() = _hasAddress
    val order: LiveData<OrderSubmit> get() = _order
    private val _contacts = MutableLiveData<MutableList<Contact>>()
    val contacts: LiveData<MutableList<Contact>> get() = _contacts
    init {
        _hasAddress.value=true
    }
    fun setOrder(order:OrderSubmit){
        _order.value=order
        if(_order.value!!.contacts==null ||_order.value!!.contacts!!.count()==0 ){
            _hasAddress.value=false
        }
    }
    fun  onSubmit(context: Context,container:ViewGroup){
        if(_order.value!!.purchaseOrderContact==null){
            showMadal(context,context.getString(R.string.contact_require))
            return
        }
        uiScope.launch {
            try {
                showWaiting(context)
                val result = OrderServiceApi.retrofitService.createOrderAsync(_order.value!!).await()
                hideWaiting()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                 else{
                    container.findNavController().navigate(R.id.orderFragment)
                }
            } catch (e: Exception) {
                displayException(context,e)
            }
        }
    }
    fun getContact(context: Context){
        uiScope.launch {
            try {
                showWaiting(context)
                val filter = Filter()
                filter.buyerId= userEntity.value!!.buyerId
                val result = ContactServiceApi.retrofitService.getsAsync(filter).await()
                if(!result.isSucess)
                {   showMadal(context,result.errorText)
                }
                else{
                    _contacts.value=result.contacts!!
                    if(_contacts.value!!.count()>0){
                        _hasAddress.value=true
                    }
                }
                hideWaiting()
            } catch (e: Exception) {
                displayException(context,e)
            }
        }
    }
}