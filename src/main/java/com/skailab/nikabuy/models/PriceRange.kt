package com.skailab.nikabuy.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PriceRange (
    var price:Double?=null,
    var toQuantity:Int?=null,
    var fromQuantity:Int?=null,
    var description:String?="",
    var isHasPriceRange:Boolean?=true,
    var unitPriceInChn:Double?=null
) : Parcelable