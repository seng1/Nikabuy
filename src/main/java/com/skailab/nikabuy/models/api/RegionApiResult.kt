package com.skailab.nikabuy.models.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.skailab.nikabuy.models.Region

data class RegionApiResult(
    @Expose
    @SerializedName("isSucess")
    val isSucess: Boolean,
    @Expose
    @SerializedName("errorText")
    val errorText: String="",
    @Expose
    @SerializedName("countries")
    val countries: MutableList<Region>?=null,
    @Expose
    @SerializedName("provinces")
    val provinces: MutableList<Region>?=null,
    @Expose
    @SerializedName("districts")
    val districts: MutableList<Region>?=null
)