package com.skailab.nikabuy.viewModels


import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skailab.nikabuy.adapter.BoxAdapter
import com.skailab.nikabuy.models.Box
import com.skailab.nikabuy.models.MultiplePayBox
import com.skailab.nikabuy.room.UserDao
import com.skailab.nikabuy.services.BoxServiceApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BoxPaymentAllViewModel( db: UserDao?) : BaseViewModel(db) {
    private val _boxes = MutableLiveData<MutableList<Box>>()
    val boxes: LiveData<MutableList<Box>> get() = _boxes
    private val _totalBox = MutableLiveData<Int>()
    val totalBox: LiveData<Int> get() = _totalBox
    private val _totalAmount = MutableLiveData<Double>()
    val totalAmount: LiveData<Double> get() = _totalAmount
    fun setBoxes(box: MutableList<Box>){
       _boxes.value=box
        _totalBox.value=box.count()
        var total:Double=0.0
        box.forEach {
            total+=it.shippingFee!!
        }
        _totalAmount.value=total

    }
    fun pay(context: Context, password:String, sheet: BottomSheetDialogFragment, adapter: BoxAdapter){
        uiScope.launch {
            try {
                showWaiting(context)
                val data: MultiplePayBox=MultiplePayBox(password,userEntity.value!!.buyerId,boxes.value)
                val result = BoxServiceApi.retrofitService.payAllAsync(data).await()
                hideWaiting()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{
                    boxes.value!!.clear()
                    adapter.notifyDataSetChanged()
                    sheet.dismiss()
                }
            } catch (e: Exception) {
                displayException(context,e)
            }
        }
    }
}