package com.skailab.nikabuy.viewModels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skailab.nikabuy.adapter.BoxAdapter
import com.skailab.nikabuy.models.Box
import com.skailab.nikabuy.models.filter.BoxFilter
import com.skailab.nikabuy.room.UserDao
import com.skailab.nikabuy.services.BoxServiceApi
import kotlinx.coroutines.launch

class BoxViewModel( db: UserDao?) : BaseViewModel(db) {
    private val _boxes = MutableLiveData<MutableList<Box>>()
    val boxes: LiveData<MutableList<Box>> get() = _boxes
    private val _showWating = MutableLiveData<Boolean>()
    val showWating: LiveData<Boolean> get() = _showWating
    val filter:BoxFilter= BoxFilter()
    private val _hasBox = MutableLiveData<Boolean>()
    val hasBox: LiveData<Boolean> get() = _hasBox
    init {
        filter.pageSize=20
        _boxes.value= mutableListOf()
        _showWating.value=false
        _hasBox.value=false
    }
    fun getBoxes(context: Context,boxAdapter: BoxAdapter){
        uiScope.launch {
            try {
                filter.buyerId=userEntity.value!!.buyerId!!
                showWaiting(context)
                val result = BoxServiceApi.retrofitService.getsAsync(filter).await()
                hideWaiting()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{
                    _boxes.value=result.boxes!!
                    boxAdapter.notifyDataSetChanged()
                    filter.page++
                    _hasBox.value=result.boxes!!.count()>0
                }
            } catch (e: Exception) {
                displayException(context,e)
            }
        }
    }
    fun  getMoreBox(context: Context,boxAdapter: BoxAdapter){
        uiScope.launch {
            try {
               _showWating.value=true
                val result = BoxServiceApi.retrofitService.getsAsync(filter).await()
                _showWating.value=false
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{
                    val index=_boxes.value!!.count()
                    _boxes.value!!.addAll(result.boxes!!)
                    boxAdapter.notifyItemRangeInserted(index,result.boxes!!.count())
                    filter.page++
                }
            } catch (e: Exception) {
                displayException(context,e)
            }
        }
    }
    fun setStatus(context: Context,boxAdapter: BoxAdapter,statusId:Int){
        filter.page=1
        filter.statusId=statusId
        _boxes.value= mutableListOf()
        boxAdapter.notifyDataSetChanged()
        getBoxes(context,boxAdapter)
    }
}