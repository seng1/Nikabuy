package com.skailab.nikabuy.services

import com.skailab.nikabuy.models.MultiplePayBox
import com.skailab.nikabuy.models.api.ApiResult
import com.skailab.nikabuy.models.api.BoxApiResult
import com.skailab.nikabuy.models.filter.BoxFilter
import kotlinx.coroutines.Deferred
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface BoxService {
    @POST("box/Gets")
    fun  getsAsync(@Body data: BoxFilter) : Deferred<BoxApiResult>
    @GET("box/pay/{id}/{buyerId}/{password}")
    fun  payAsync(@Path("id") id:Int,@Path("buyerId") buyerId:Int,@Path("password") password:String) : Deferred<ApiResult>
    @POST("box/PayAll")
    fun  payAllAsync(@Body data: MultiplePayBox) : Deferred<ApiResult>
}
object BoxServiceApi{
    val retrofitService:BoxService by lazy { retrofit.create(BoxService::class.java)}
}
