package com.skailab.nikabuy.models.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ApiResult(
    @Expose
    @SerializedName("isSucess")
    val isSucess: Boolean,
    @Expose
    @SerializedName("errorText")
    val errorText: String=""
)