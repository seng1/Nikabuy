package com.skailab.nikabuy.services


import com.skailab.nikabuy.models.api.*
import com.skailab.nikabuy.models.filter.AudioFilter
import com.skailab.nikabuy.models.filter.OtFilter
import com.skailab.nikabuy.models.filter.ProductFilter
import kotlinx.coroutines.Deferred
import retrofit2.http.Body
import retrofit2.http.POST

interface ProductService {
    @POST("Product/GetProductListing")
    fun  getProductListingAsync(@Body data: ProductFilter) : Deferred<ProductApiResult>
    @POST("Product/GetProductImageSearchFromBaseString")
    fun  getProductImageSearchFromBaseStringAsync(@Body data: OtFilter) : Deferred<ProductImageSearchBaseStringApiResult>
    @POST("Product/GetOtProducts")
    fun  GetOtProductsAsync(@Body data: OtFilter) : Deferred<ProductApiResult>
    @POST("Product/GetProductAudioSearchFromBaseString")
    fun  getProductAudioSearchFromBaseStringStringAsync(@Body data: AudioFilter) : Deferred<ProductAudioSearchBaseStringApiResult>
    @POST("Product/GetProductsTextSearch")
    fun  getProductsTextSearchAsync(@Body data: OtFilter) : Deferred<ProductTextApiResult>
    @POST("Product/GetProductDetail")
    fun  getProductDetailAsync(@Body data: OtFilter) : Deferred<ProductDetailApiResult>
    @POST("Product/GetSuggestGoods")
    fun  GetSuggestGoods(@Body data: OtFilter) : Deferred<ProductApiResult>

}
object ProductServiceApi{
    val retrofitService:ProductService by lazy { retrofit.create(ProductService::class.java)}
}
