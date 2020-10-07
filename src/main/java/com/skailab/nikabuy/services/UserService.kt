package com.skailab.nikabuy.services

import com.skailab.nikabuy.models.Buyer
import com.skailab.nikabuy.models.PhoneVerificationCodeAnwserResult
import com.skailab.nikabuy.models.Register
import com.skailab.nikabuy.models.api.*
import kotlinx.coroutines.Deferred
import retrofit2.http.*

interface UserService {
    @GET("User/SendSmsForRegisterUser")
    fun  SendSmsForRegisterUserAsync(@Query("phoneNumber") phoneNumber:String) : Deferred<PhoneVerificationCodeAnwserResult>
    @POST("User/Register")
    fun  RegisterAsync(@Body data:Register) : Deferred<UserApiResult>
    @GET("User/GetBuyerDashboard/{buyerId}")
    fun  getBuyerDashboardAsync(@Path("buyerId") buyerId:Int) : Deferred<BuyerDashboardApiResult>
    @GET("User/GetBuyer/{buyerId}")
    fun  getBuyerAsync(@Path("buyerId") buyerId:Int) : Deferred<BuyerApiResult>
    @POST("User/UpdateBuyer")
    fun  updateBuyerAsync(@Body data:Buyer) : Deferred<BuyerApiResult>
    @GET("User/UpdateSaleMan/{buyerId}/{salemanCode}")
    fun  updateSaleManAsync(@Path("buyerId") buyerId:Int,@Path("salemanCode") salemanCode:String) : Deferred<BuyerApiResult>
    @GET("User/signIn")
    fun  signInAsync(@Query("userName")userName:String,@Query("password")password:String) : Deferred<UserApiResult>
    @GET("User/SendVerificationCode")
    fun  sendVerificationCodeAsync(@Query("phoneNumber")phoneNumber:String) : Deferred<VerificationCodeApiResult>
    @GET("User/ResetPassword")
    fun  resetPasswordAsync(@Query("userId")userId:String,@Query("password")password:String) : Deferred<UserApiResult>
    @GET("User/RegisterOrLoginWithFacebook/{id}")
    fun  registerOrLoginWithFacebookAsync(@Path("id")id:String, @Query("name")name:String) : Deferred<UserApiResult>
    @GET("User/GetAzureB2CUser/{id}")
    fun  getAzureB2CUserAsync(@Path("id")id:String) : Deferred<UserApiResult>
    @GET("User/GetAccountLinks/{buyerId}")
    fun  getAccountLinksAsync(@Path("buyerId")buyerId:Int) : Deferred<AccountLinkApiResult>
    @POST("User/LinkMobileAccount/{buyerId}")
    fun  linkMobileAccountAsync(@Path("buyerId")buyerId:Int,@Body data:Register) : Deferred<ApiResult>
    @POST("User/LinkFacebookAccount/{buyerId}/{facebookUserId}")
    fun  linkFacebookAccount(@Path("buyerId")buyerId:Int,@Path("facebookUserId")facebookUserId:String,@Query("name")name:String) : Deferred<ApiResult>
    @GET("User/CheckAllAccountLink")
    fun  checkAllAccountLinkAsync() : Deferred<CheckAllAccountLinkApiResult>
    @GET("User/GetSaleMans")
    fun  getSaleMansAsync() : Deferred<SaleManApiResult>
}
object UserServiceApi{
    val retrofitService:UserService by lazy { retrofit.create(UserService::class.java)}
}
