package com.skailab.nikabuy.viewModels

import android.app.Activity
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skailab.nikabuy.models.MulitpleOrderPayment
import com.skailab.nikabuy.models.Order
import com.skailab.nikabuy.room.UserDao
import com.skailab.nikabuy.services.OrderServiceApi
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import kotlinx.coroutines.launch

class OrderPayAllViewModel( db: UserDao?) : BaseViewModel(db) {
    private val _orders = MutableLiveData<MutableList<Order>>()
    val orders: LiveData<MutableList<Order>> get() = _orders
    private val _totalOrder= MutableLiveData<Int>()
    val totalOrder: LiveData<Int> get() = _totalOrder
    private val _totalAmount = MutableLiveData<Double>()
    val totalAmount: LiveData<Double> get() = _totalAmount
    fun setOrder( order:MutableList<Order> ){
        _orders.value=order
        _totalOrder.value=order.count()
        var amount:Double=0.0
        order.forEach {
            amount+=it.total!!
        }
        _totalAmount.value=amount
    }
    fun pay(password:String, adapter:SectionedRecyclerViewAdapter,activity: Activity,context: Context,sheet: BottomSheetDialogFragment){
        uiScope.launch {
            try {
                showWaiting(context)
                val data: MulitpleOrderPayment = MulitpleOrderPayment(userEntity.value!!.buyerId,password,orders.value)
                val result = OrderServiceApi.retrofitService.payAllAsync(data).await()
                hideWaiting()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{
                   orders.value!!.clear()
                    adapter.removeAllSections()
                    adapter.notifyDataSetChanged()
                    sheet.dismiss()
                }
            } catch (e: Exception) {
                displayException(context,e)
            }
        }
    }
}