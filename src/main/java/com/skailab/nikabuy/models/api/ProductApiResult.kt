package com.skailab.nikabuy.models.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skailab.nikabuy.models.Product
import com.skailab.nikabuy.models.Product1688
import com.skailab.nikabuy.models.ProductTaobao

data class ProductApiResult(
    @Expose
    @SerializedName("products")
    val products: MutableList<Product>?,
    @Expose
    @SerializedName("isSucess")
    val isSucess: Boolean,
    @Expose
    @SerializedName("errorText")
    val errorText: String=""

)