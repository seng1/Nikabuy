package com.skailab.nikabuy.models.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skailab.nikabuy.models.User

data class VerificationCodeApiResult(
    @Expose
    @SerializedName("isSucess")
    val isSucess: Boolean,
    @Expose
    @SerializedName("errorText")
    val errorText: String?="",
    @Expose
    @SerializedName("verificationCode")
    val verificationCode: String?="",
    @Expose
    @SerializedName("userId")
    val userId: String?=""

)