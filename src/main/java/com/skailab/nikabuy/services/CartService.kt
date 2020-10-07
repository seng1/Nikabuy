package com.skailab.nikabuy.services

import com.skailab.nikabuy.models.Cart
import com.skailab.nikabuy.models.Product1688Cart
import com.skailab.nikabuy.models.ProductCart
import com.skailab.nikabuy.models.TaobaoCart
import com.skailab.nikabuy.models.api.CartApiResult
import com.skailab.nikabuy.models.api.CreateOrderSubmitApiResult
import com.skailab.nikabuy.models.api.ShopCartApiResult
import kotlinx.coroutines.Deferred
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CartService {
    @POST("cart/Add")
    fun  addAsync(@Body data: ProductCart) : Deferred<CartApiResult>
    @GET("cart/Gets/{buyerId}")
    fun  getsAsync(@Path("buyerId") buyerId:Int) : Deferred<CartApiResult>
    @GET("cart/GetCartsGroupByShop/{buyerId}")
    fun  getCartsGroupByShopAsync(@Path("buyerId") buyerId:Int) : Deferred<ShopCartApiResult>
    @GET("cart/Delete/{buyerId}/{cartId}")
    fun  deleteAsync(@Path("buyerId") buyerId:Int,@Path("cartId") cartId:Int) : Deferred<CartApiResult>
    @POST("cart/update")
    fun  updateAsync(@Body data: Cart) : Deferred<CartApiResult>
    @GET("cart/clear/{buyerId}")
    fun  clearAsync(@Path("buyerId") buyerId:Int) : Deferred<CartApiResult>
    @POST("product/CreateTaobaoCart")
    fun  createTaobaoCartAsync(@Body data: TaobaoCart) : Deferred<CreateOrderSubmitApiResult>
    @POST("product/CreateAlibabaCart")
    fun  createAlibabaCartAsync(@Body data: Product1688Cart) : Deferred<CreateOrderSubmitApiResult>

}
object CartServiceApi{
    val retrofitService:CartService by lazy { retrofit.create(CartService::class.java)}
}
