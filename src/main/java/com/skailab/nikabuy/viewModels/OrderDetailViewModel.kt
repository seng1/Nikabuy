package com.skailab.nikabuy.viewModels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skailab.nikabuy.models.OrderDetail
import com.skailab.nikabuy.models.filter.OrderDetailFilter
import com.skailab.nikabuy.room.UserDao
import com.skailab.nikabuy.services.OrderServiceApi
import kotlinx.coroutines.launch

class OrderDetailViewModel( db: UserDao?) : BaseViewModel(db) {
    private val _order = MutableLiveData<OrderDetail>()
    val order: LiveData<OrderDetail> get() = _order
    var orderId:Int=0
    init {
        _order.value= OrderDetail()
    }
    fun getOrder(context:Context){
        uiScope.launch {
            try {
                showWaiting(context)
                val filter= OrderDetailFilter()
                filter.buyerId=userEntity.value!!.buyerId
                filter.page=1
                filter.pageSize=0
                filter.orderId=orderId
                val result = OrderServiceApi.retrofitService.getOrderAsync(filter).await()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{
                  _order.value=result.order
                }
                hideWaiting()
            } catch (e: Exception) {
                displayException(context,e)
            }
        }
    }
}