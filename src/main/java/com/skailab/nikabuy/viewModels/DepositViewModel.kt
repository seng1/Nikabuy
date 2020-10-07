package com.skailab.nikabuy.viewModels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skailab.nikabuy.adapter.DepositAdapter
import com.skailab.nikabuy.models.Deposit
import com.skailab.nikabuy.models.filter.DepositFilter
import com.skailab.nikabuy.room.UserDao
import com.skailab.nikabuy.services.DepositServiceApi
import kotlinx.coroutines.launch

class DepositViewModel( db: UserDao?) : BaseViewModel(db) {
    val filter:DepositFilter= DepositFilter()
    private val _deposits = MutableLiveData<MutableList<Deposit>>()
    val deposits: LiveData<MutableList<Deposit>> get() = _deposits
    private val _accountBalance = MutableLiveData<Double>()
    val accountBalance: LiveData<Double> get() = _accountBalance
    private val _showWaiting = MutableLiveData<Boolean>()
    val showWaiting: LiveData<Boolean> get() = _showWaiting
    init {
        filter.isGetBalance=true
        filter.pageSize=10
        _deposits.value= mutableListOf()
        _accountBalance.value=0.0
        _showWaiting.value=false
    }
    fun getDeposits(context:Context){
        filter.buyerId=userEntity.value!!.buyerId!!
        uiScope.launch {
            try {
                showWaiting(context)
                val result = DepositServiceApi.retrofitService.getsAsync(filter).await()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{
                   _deposits.value=result.deposits
                    _accountBalance.value=result.accountBalance
                    filter.isGetBalance=false
                    filter.page+=1
                }
                hideWaiting()
            } catch (e: Exception) {
                displayException(context,e)

            }
        }
    }
    fun getMoreDeposit(context: Context,adapter: DepositAdapter){
        uiScope.launch {
            try {
               _showWaiting.value=true
                val result = DepositServiceApi.retrofitService.getsAsync(filter).await()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{
                    val index=_deposits.value!!.count()
                    _deposits.value!!.addAll(result.deposits!!)
                    adapter.notifyItemRangeInserted(index,result.deposits!!.count())
                    filter.page+=1
                }
                _showWaiting.value=false
            } catch (e: Exception) {
                displayException(context,e)

            }
        }
    }

}