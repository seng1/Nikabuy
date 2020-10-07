package com.skailab.nikabuy.models.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CheckAllAccountLinkApiResult(
    @Expose
    @SerializedName("isSucess")
    val isSucess: Boolean,
    @Expose
    @SerializedName("errorText")
    val errorText: String="",
    @Expose
    @SerializedName("isAllLink")
    val isAllLink: Boolean
)