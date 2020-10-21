package com.skailab.nikabuy.viewModels


import android.content.Context
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skailab.nikabuy.adapter.BoxAdapter
import com.skailab.nikabuy.models.Box
import com.skailab.nikabuy.room.UserDao
import com.skailab.nikabuy.services.BoxServiceApi
import kotlinx.android.synthetic.main.fragment_box_payment.view.*
import kotlinx.coroutines.launch

class BoxPaymentViewModel( db: UserDao?) : BaseViewModel(db) {
    private val _box: MutableLiveData<Box> = MutableLiveData<Box>()
    val box: LiveData<Box> get() = _box
    fun setBox(box: Box){
        _box.value=box
    }
    fun pay(context: Context, password:String, sheet: BottomSheetDialogFragment, holder: BoxAdapter.BoxItemViewHolder){
        uiScope.launch {
            try {
                showWaiting(context)
                val result = BoxServiceApi.retrofitService.payAsync(_box.value!!.id,userEntity.value!!.buyerId,password).await()
                hideWaiting()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{
                    _box.value!!.isPaid=true
                    holder.itemView.btnPay.visibility=View.GONE
                    sheet.dismiss()
                }

            } catch (e: Exception) {
                displayException(context,e)
            }
        }
    }
}