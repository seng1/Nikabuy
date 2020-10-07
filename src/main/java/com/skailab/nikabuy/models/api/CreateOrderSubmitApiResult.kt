package com.skailab.nikabuy.models.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skailab.nikabuy.models.OrderSubmit

data class CreateOrderSubmitApiResult(
    @Expose
    @SerializedName("isSucess")
    val isSucess: Boolean,
    @Expose
    @SerializedName("errorText")
    val errorText: String="",
    @Expose
    @SerializedName("order")
    val order: OrderSubmit?=null
)