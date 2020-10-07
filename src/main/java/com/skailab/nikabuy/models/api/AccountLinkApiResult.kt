package com.skailab.nikabuy.models.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skailab.nikabuy.models.User

data class AccountLinkApiResult(
    @Expose
    @SerializedName("isSucess")
    val isSucess: Boolean,
    @Expose
    @SerializedName("errorText")
    val errorText: String="",
    @Expose
    @SerializedName("users")
    val users: MutableList<User>?=null,
    @Expose
    @SerializedName("isMobileAdded")
    val isMobileAdded: Boolean,
    @Expose
    @SerializedName("isFacebookAdded")
    val isFacebookAdded: Boolean

)