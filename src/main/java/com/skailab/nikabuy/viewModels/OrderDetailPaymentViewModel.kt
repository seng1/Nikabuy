package com.skailab.nikabuy.viewModels

import android.content.Context
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skailab.nikabuy.R
import com.skailab.nikabuy.databinding.FragmentOrderDetailBinding
import com.skailab.nikabuy.models.OrderDetail
import com.skailab.nikabuy.room.UserDao
import com.skailab.nikabuy.services.OrderServiceApi
import kotlinx.coroutines.launch

class OrderDetailPaymentViewModel( db: UserDao?) : BaseViewModel(db) {
    private val _order = MutableLiveData<OrderDetail>()
    val order: LiveData<OrderDetail> get() = _order
    fun setOrder(order:OrderDetail){
        _order.value=order
    }
    fun pay(context: Context, password:String, sheet: BottomSheetDialogFragment,orderbinding: FragmentOrderDetailBinding){
        uiScope.launch {
            try {
                showWaiting(context)
                val result = OrderServiceApi.retrofitService.payAsync(_order.value!!.id!!,password).await()
                hideWaiting()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{
                    _order.value!!.orderStatus!!.title=context.getString(R.string.to_be_order)
                    _order.value!!.showPayButton=false
                    orderbinding.btnPay.visibility=View.GONE
                    orderbinding.lblStatus.text=_order.value!!.orderStatus!!.title
                    sheet.dismiss()
                }

            } catch (e: Exception) {
                displayException(context,e)
            }
        }
    }

}