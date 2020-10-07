package com.skailab.nikabuy.services

import com.skailab.nikabuy.models.api.*
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Path

interface PaymentService {
    @GET("payment/SetPaymentPassword/{buyerId}/{password}")
    fun  setPaymentPasswordAsync(@Path("buyerId") buyerId:Int,@Path("password") password:String) : Deferred<ApiResult>
}
object PaymentServiceApi{
    val retrofitService:PaymentService by lazy { retrofit.create(PaymentService::class.java)}
}
