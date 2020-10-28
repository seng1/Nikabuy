package com.skailab.nikabuy.services

import com.skailab.nikabuy.models.Contact
import com.skailab.nikabuy.models.api.ApiResult
import com.skailab.nikabuy.models.api.ContactApiResult
import com.skailab.nikabuy.models.api.RegionApiResult
import com.skailab.nikabuy.models.filter.Filter
import kotlinx.coroutines.Deferred
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ContactService {

    @POST("contact/Gets")
    fun  getsAsync(@Body data: Filter) : Deferred<ContactApiResult>
    @GET("contact/Delete/{id}")
    fun  deleteAsync(@Path("id") id:Int) : Deferred<ApiResult>
    @POST("contact/GetRegions")
    fun  getRegionsAsync(@Body data: Filter) : Deferred<RegionApiResult>
    @POST("contact/CreateContact/{buyerId}")
    fun  createContactAsync(@Path("buyerId") buyerId:Int,@Body data: Contact) : Deferred<ApiResult>
    @POST("contact/UpdateContact/{buyerId}")
    fun  updateContactAsync(@Path("buyerId") buyerId:Int,@Body data: Contact) : Deferred<ApiResult>
}
object ContactServiceApi{
    val retrofitService:ContactService by lazy { retrofit.create(ContactService::class.java)}
}
