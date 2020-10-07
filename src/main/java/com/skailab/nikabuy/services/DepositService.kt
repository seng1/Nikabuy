package com.skailab.nikabuy.services

import com.skailab.nikabuy.models.Deposit
import com.skailab.nikabuy.models.api.*
import com.skailab.nikabuy.models.filter.DepositFilter
import com.skailab.nikabuy.models.filter.Filter
import kotlinx.coroutines.Deferred
import retrofit2.http.Body
import retrofit2.http.POST

interface DepositService {
    @POST("deposit/Gets")
    fun  getsAsync(@Body data: DepositFilter) : Deferred<DepositApiResult>
    @POST("deposit/GetBankAccounts")
    fun  getBankAccountsAsync(@Body data: Filter) : Deferred<BankAccountApiResult>
    @POST("deposit/UploadDepositImage")
    fun  uploadDepositImageAsync(@Body data: Deposit) : Deferred<DepositApiResult>
    @POST("deposit/CreateDeposit")
    fun  createDepositAsync(@Body data: Deposit) : Deferred<DepositApiResult>
}
object DepositServiceApi{
    val retrofitService:DepositService by lazy { retrofit.create(DepositService::class.java)}
}
