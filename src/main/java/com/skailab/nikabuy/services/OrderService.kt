package com.skailab.nikabuy.services

import com.skailab.nikabuy.models.MulitpleOrderPayment
import com.skailab.nikabuy.models.OrderSubmit
import com.skailab.nikabuy.models.ProductCartParcelize
import com.skailab.nikabuy.models.api.*
import com.skailab.nikabuy.models.filter.Filter
import com.skailab.nikabuy.models.filter.OrderDetailFilter
import com.skailab.nikabuy.models.filter.OrderFilter
import kotlinx.coroutines.Deferred
import retrofit2.http.*

interface OrderService {

    @POST("order/GetCreateOrderSubmitFromProductCart")
    fun  getCreateOrderSubmitFromProductCartAsync(@Body data: ProductCartParcelize) : Deferred<CreateOrderSubmitApiResult>
    @POST("order/GetCreateOrderSubmitFromCarts")
    fun  getCreateOrderSubmitFromCartsAsync(@Body data: ProductCartParcelize) : Deferred<CreateOrderSubmitApiResult>
    @POST("order/CreateOrder")
    fun  createOrderAsync(@Body data: OrderSubmit) : Deferred<CreateOrderSubmitApiResult>
    @POST("order/GetTotalStatuses")
    fun  getTotalStatusesAsync(@Body data: Filter) : Deferred<StatusTotalApiResult>
    @POST("order/GetOrders")
    fun  getOrdersAsync(@Body data: OrderFilter) : Deferred<OrderApiResult>
    @POST("order/GetOrder")
    fun  getOrderAsync(@Body data: OrderDetailFilter) : Deferred<OrderDetailApiResult>
    @GET("order/PrintRemark/{orderId}")
    fun  printRemarkAsync(@Path("orderId") orderId:Int,@Query("label")label:String) : Deferred<ApiResult>
    @GET("order/CancelOrder/{orderId}")
    fun  cancelOrderAsync(@Path("orderId") orderId:Int) : Deferred<ApiResult>
    @GET("order/ReOrder/{orderId}")
    fun  reOrderAsync(@Path("orderId") orderId:Int) : Deferred<ApiResult>
    @GET("order/Pay/{orderId}/{password}")
    fun  payAsync(@Path("orderId") orderId:Int,@Path("password") password:String) : Deferred<ApiResult>
    @POST("order/PayAll")
    fun  payAllAsync(@Body data: MulitpleOrderPayment) : Deferred<ApiResult>
}
object OrderServiceApi{
    val retrofitService:OrderService by lazy { retrofit.create(OrderService::class.java)}
}
