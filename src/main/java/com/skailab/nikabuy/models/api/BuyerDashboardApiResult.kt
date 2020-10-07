package com.skailab.nikabuy.models.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skailab.nikabuy.models.BuyerDashboard

data class BuyerDashboardApiResult(
    @Expose
    @SerializedName("isSucess")
    val isSucess: Boolean,
    @Expose
    @SerializedName("errorText")
    val errorText: String="",
    @Expose
    @SerializedName("buyerDashboard")
    val buyerDashboard: BuyerDashboard?=null
)