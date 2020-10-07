package com.skailab.nikabuy.models.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skailab.nikabuy.models.Deposit

data class DepositApiResult(
    @Expose
    @SerializedName("isSucess")
    val isSucess: Boolean,
    @Expose
    @SerializedName("errorText")
    val errorText: String="",
    @Expose
    @SerializedName("deposits")
    val deposits: MutableList<Deposit>?=null,
    @Expose
    @SerializedName("accountBalance")
    val accountBalance: Double?=0.0,
    @Expose
    @SerializedName("deposit")
    val deposit: Deposit?=null
)