package com.skailab.nikabuy.models.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skailab.nikabuy.models.Product1688
import com.skailab.nikabuy.models.ProductDetail
import com.skailab.nikabuy.models.ProductTaobao


data class ProductDetailApiResult(
    @Expose
    @SerializedName("product")
    val product: ProductDetail?=null,
    @Expose
    @SerializedName("isSucess")
    val isSucess: Boolean,
    @Expose
    @SerializedName("errorText")
    val errorText: String="",
    @Expose
    @SerializedName("productTaobao")
    val productTaobao: ProductTaobao?=null,
    @Expose
    @SerializedName("product1688")
    val product1688: Product1688?=null
)