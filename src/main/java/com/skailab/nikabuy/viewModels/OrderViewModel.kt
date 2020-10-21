package com.skailab.nikabuy.viewModels

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.skailab.nikabuy.R
import com.skailab.nikabuy.models.Order
import com.skailab.nikabuy.models.OrderForBindingList
import com.skailab.nikabuy.models.OrderStatus
import com.skailab.nikabuy.models.filter.Filter
import com.skailab.nikabuy.models.filter.OrderFilter
import com.skailab.nikabuy.room.UserDao
import com.skailab.nikabuy.services.OrderServiceApi
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_buyer_info.view.*
import kotlinx.android.synthetic.main.section_order_footer.view.*
import kotlinx.android.synthetic.main.section_order_header.view.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OrderViewModel( db: UserDao?) : BaseViewModel(db) {
    private val _orderStatues = MutableLiveData<MutableList<OrderStatus>>()
    val orderStatues: LiveData<MutableList<OrderStatus>> get() = _orderStatues
    private val _filter:OrderFilter= OrderFilter()
    private val _orders = MutableLiveData<MutableList<Order>>()
    private val _newRequestedOrders = MutableLiveData<MutableList<Order>>()
    val newRequestedOrders: LiveData<MutableList<Order>> get() = _newRequestedOrders
    val orders: LiveData<MutableList<Order>> get() = _orders
    private val _hasOrder = MutableLiveData<Boolean>()
    val hasOrder: LiveData<Boolean> get() = _hasOrder
    private val _showWaiting = MutableLiveData<Boolean>()
    val showWaiting: LiveData<Boolean> get() = _showWaiting
    var previouseInsertIndex:Int=0
    var isBack:Boolean=false
    private val _showCenterWaiting = MutableLiveData<Boolean>()
    val showCenterWaiting: LiveData<Boolean> get() = _showCenterWaiting
    private val _selectedStatusId = MutableLiveData<Int>()
    val selectedStatusId: LiveData<Int> get() = _selectedStatusId
    init {
        _orderStatues.value= mutableListOf()
        _filter.statusId=0
        _orders.value= mutableListOf()
        _hasOrder.value=true
        _newRequestedOrders.value= mutableListOf()
        _filter.pageSize=10
        _showWaiting.value= false
        _showCenterWaiting.value=false
        _selectedStatusId.value=_filter.statusId
    }
    fun getOrderStatuses(context:Context){
        uiScope.launch {
            try {
                val filter= Filter()
                filter.buyerId=userEntity.value!!.buyerId
                filter.page=1
                filter.pageSize=0
                val result = OrderServiceApi.retrofitService.getTotalStatusesAsync(filter).await()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{
                  _orderStatues.value=result.orderStatuses!!

                }
            } catch (e: Exception) {
                displayException(context,e)
            }
        }
    }
    fun onGetMoreOrder(){
        uiScope.launch {
            try {
                if(_orders.value!!.count()>0){
                    _showWaiting.value=true
                }
                else{
                    _showCenterWaiting.value=true
                }

                _filter.buyerId=userEntity.value!!.buyerId
                val result = OrderServiceApi.retrofitService.getOrdersAsync(_filter).await()
                hideWaiting()
                if(!result.isSucess){
                    //showMadal(context,result.errorText)
                }
                else{
                    previouseInsertIndex=_orders.value!!.count()
                    _orders.value!!.addAll(result.orders!!)
                    _hasOrder.value=_orders.value!!.count()>0
                    _newRequestedOrders.value= result.orders!!
                    _filter.page+=1
                }
                _showWaiting.value=false
                _showCenterWaiting.value=false

            } catch (e: Exception) {
               // displayException(context,e)
                _showWaiting.value=false
            }
        }
    }
    fun  onGetOrders(context: Context){
        uiScope.launch {
            try {
                showWaiting(context)
               _filter.buyerId=userEntity.value!!.buyerId
                val result = OrderServiceApi.retrofitService.getOrdersAsync(_filter).await()

                if(!result.isSucess){
                    showMadal(context,result.errorText)
                    hideWaiting()
                }
                else{
                    previouseInsertIndex=_orders.value!!.count()
                    _orders.value!!.addAll(result!!.orders!!)
                    _newRequestedOrders.value=result!!.orders!!
                    _hasOrder.value=result.orders!!.count()>0
                    _filter.page+=1

                }
                hideWaiting()

            } catch (e: Exception) {
                displayException(context,e)
                _showWaiting.value=false
            }
        }
    }
    fun setStatus(statusId:Int,context: Context){
        _filter.statusId=statusId
        _selectedStatusId.value=statusId
       _orders.value= mutableListOf()
        _filter.page=1
        onGetOrders(context)
    }
    fun printRemark(context: Context,orderId:Int,label:String){
        uiScope.launch {
            try {
                showWaiting(context)
                val result = OrderServiceApi.retrofitService.printRemarkAsync(orderId,label).await()
                hideWaiting()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }


            } catch (e: Exception) {
                displayException(context,e)
            }
        }
    }
    fun  cancelOrder(context: Context,order:OrderForBindingList){
        uiScope.launch {
            try {
                showWaiting(context)
                val result = OrderServiceApi.retrofitService.cancelOrderAsync(order.order!!.id!!).await()
                hideWaiting()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else
                {
                  order.order!!.orderStatus!!.title=context.getString(R.string.customer_cancel)
                  order.order!!.showCancelButton=false
                  order.order!!.showPayButton=false
                  if(order.headerViewHolder!=null){
                      order.headerViewHolder!!.itemView.txtStatus.setText(order.order!!.orderStatus!!.title)
                  }
                  if(order.footerViewHolder!=null){
                      order.footerViewHolder!!.itemView.btnPay.visibility=View.GONE
                      order.footerViewHolder!!.itemView.btnCancel.visibility=View.GONE
                  }
                }

            } catch (e: Exception) {
                displayException(context,e)
            }
        }
    }
    fun reOrder(context: Context,container:ViewGroup,orderId:Int){
        uiScope.launch {
            try {
               showWaiting(context)
                val result = OrderServiceApi.retrofitService.reOrderAsync(orderId).await()
                hideWaiting()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{
                    MaterialAlertDialogBuilder(context)
                        .setTitle(context.resources.getString(R.string.app_name))
                        .setMessage(context.getString(R.string.order_add_to_cart_sccuess_order_now_question))
                        .setNeutralButton(context.getString(R.string.yes)) { dialog, which ->
                            container.findNavController().navigate(R.id.cardFragment)
                        }.setPositiveButton(context.getString(R.string.no)) { dialog, which ->
                        }
                        .show()
                }

            } catch (e: Exception) {
                displayException(context,e)
            }
        }
    }
}