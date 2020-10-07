package com.skailab.nikabuy.viewModels

import android.content.Context
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.skailab.nikabuy.R
import com.skailab.nikabuy.databinding.FragmentNewDepositBinding
import com.skailab.nikabuy.models.BankAccount
import com.skailab.nikabuy.models.Deposit
import com.skailab.nikabuy.models.DepositAccount
import com.skailab.nikabuy.models.filter.Filter
import com.skailab.nikabuy.room.UserDao
import com.skailab.nikabuy.services.DepositServiceApi
import kotlinx.coroutines.launch

class NewDepositViewModel( db: UserDao?) : BaseViewModel(db) {
    private val _deposit = MutableLiveData<Deposit>()
    val deposit: LiveData<Deposit> get() = _deposit
    private val _bankAccounts = MutableLiveData<MutableList<BankAccount>>()
    val bankAccounts: LiveData<MutableList<BankAccount>> get() = _bankAccounts
    private val _hasDate = MutableLiveData<Boolean>()
    val hasDate: LiveData<Boolean> get() = _hasDate
    private val _imageUploaded = MutableLiveData<Boolean>()
    val imageUploaded: LiveData<Boolean> get() = _imageUploaded
    init {
        _deposit.value= Deposit()
        _deposit.value!!.depositAccount= DepositAccount()
        _bankAccounts.value= mutableListOf()
        _hasDate.value=false
        _imageUploaded.value=false
    }
    fun getBankAccounts(context:Context){
        val filter = Filter()
        filter.buyerId=userEntity.value!!.buyerId
        uiScope.launch {
            try {
                showWaiting(context)
                val result = DepositServiceApi.retrofitService.getBankAccountsAsync(filter).await()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{
                   _bankAccounts.value=result.accounts
                }
                hideWaiting()
            } catch (e: Exception) {
                displayException(context,e)

            }
        }
    }
    fun setHasDate(hasDate:Boolean){
        _hasDate.value=hasDate
    }
    fun uploadImage(binding: FragmentNewDepositBinding,context: Context){
        uiScope.launch {
            try {
                showWaiting(context)
                val result = DepositServiceApi.retrofitService.uploadDepositImageAsync(_deposit.value!!).await()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{
                    if(!result.deposit!!.dateText.isNullOrEmpty()){
                        binding.txtDate.text= result.deposit.dateText
                        _hasDate.value=true
                    }
                    else{
                        binding.txtDate.text= ""
                        _hasDate.value=false
                    }
                    if(!result.deposit.depositAccount!!.bankTransactionNumber.isNullOrEmpty()){
                        _deposit.value!!.depositAccount!!.bankTransactionNumber= result.deposit.depositAccount!!.bankTransactionNumber
                        binding.txtTransaction.setText(result.deposit.depositAccount!!.bankTransactionNumber.toString())
                    }
                    else{
                        _deposit.value!!.depositAccount!!.bankTransactionNumber=""
                        binding.txtTransaction.setText("")
                    }
                    if(result.deposit.amount!=null && result.deposit.amount!!>0){
                        _deposit.value!!.amount= result.deposit.amount
                        binding.txtAmount.setText(result.deposit.amount.toString())
                    }
                    else{
                        _deposit.value!!.amount=null
                        binding.txtAmount.setText("")
                    }
                    if(!result.deposit.remark.isNullOrEmpty()){
                        _deposit.value!!.remark= result.deposit.remark
                        binding.txtRemark.setText(result.deposit.remark.toString())
                    }
                    else{
                        _deposit.value!!.remark= ""
                        binding.txtRemark.setText("")
                    }
                    if(result.deposit.depositAccount!!.bankAccountId!=null){
                        _deposit.value!!.depositAccount!!.bankAccountId= result.deposit.depositAccount!!.bankAccountId
                        binding.rdBankAccount.check(_deposit.value!!.depositAccount!!.bankAccountId!!)
                    }
                    _deposit.value!!.depositAccount!!.receiptImage= result.deposit.depositAccount!!.receiptImage
                    _imageUploaded.value=true
                }
                hideWaiting()
            } catch (e: Exception) {
                binding.txtDate.text = ""
                binding.txtTransaction.setText("")
                binding.txtRemark.setText("")
                _deposit.value!!.depositAccount!!.receiptImage=""
                binding.txtAmount.setText("")
                displayException(context,e)

            }
        }
    }
    fun onSubmit(context: Context,container:ViewGroup){
        uiScope.launch {
            try {
                _deposit.value!!.buyerId= userEntity.value!!.buyerId
                showWaiting(context)
                val result = DepositServiceApi.retrofitService.createDepositAsync(_deposit.value!!).await()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{
                    container.findNavController().navigate(R.id.depositFragment)
                }
                hideWaiting()
            } catch (e: Exception) {
                displayException(context,e)

            }
        }
    }
}