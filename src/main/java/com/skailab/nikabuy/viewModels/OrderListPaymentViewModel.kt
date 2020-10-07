package com.skailab.nikabuy.viewModels

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skailab.nikabuy.R
import com.skailab.nikabuy.models.OrderForBindingList
import com.skailab.nikabuy.room.UserDao
import com.skailab.nikabuy.services.OrderServiceApi
import kotlinx.android.synthetic.main.section_order_footer.view.*
import kotlinx.android.synthetic.main.section_order_header.view.*
import kotlinx.coroutines.launch

class OrderListPaymentViewModel( db: UserDao?) : BaseViewModel(db) {
    private val _order = MutableLiveData<OrderForBindingList>()
    val order: LiveData<OrderForBindingList> get() = _order
    init {

    }
    fun setOrder(order:OrderForBindingList){
        _order.value=order
    }
    fun pay(context: Context,password:String,sheet: BottomSheetDialogFragment){
        uiScope.launch {
            try {
                showWaiting(context)
                val result = OrderServiceApi.retrofitService.payAsync(_order.value!!.order!!.id!!,password).await()
                hideWaiting()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{
                    _order.value!!.order!!.orderStatus!!.title=context.getString(R.string.to_be_order)
                    _order.value!!.order!!.showPayButton=false
                    _order.value!!.order!!.showCancelButton=false
                    if(_order.value!!.footerViewHolder!=null){
                        _order.value!!.footerViewHolder!!.rootView.btnCancel.visibility=View.GONE
                        _order.value!!.footerViewHolder!!.rootView.btnPay.visibility=View.GONE
                    }
                    if(_order.value!!.headerViewHolder!=null){
                        _order.value!!.headerViewHolder!!.itemView.txtStatus.setText(_order.value!!.order!!.orderStatus!!.title)
                    }
                   sheet.dismiss()
                }

            } catch (e: Exception) {
                displayException(context,e)
            }
        }
    }

}