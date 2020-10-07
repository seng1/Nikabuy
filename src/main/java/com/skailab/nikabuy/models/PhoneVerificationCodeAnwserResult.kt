package com.skailab.nikabuy.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PhoneVerificationCodeAnwserResult(
    @Expose
    @SerializedName("verificationCode")
    val verificationCode: String?="",
    @Expose
    @SerializedName("isSucess")
    val isSucess: Boolean,
    @Expose
    @SerializedName("errorText")
    val errorText: String

)