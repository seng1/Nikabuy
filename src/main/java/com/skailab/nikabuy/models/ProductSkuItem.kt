package com.skailab.nikabuy.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductSkuItem(
    var quantity:Int?=0,
    var price:Double?=0.0,
    var imageUrl:String?="",
    var skuText:String?="",
    var orignalSkuText:String?="",
    var unitPriceInChn:Double?=0.0,
    var isHasImage:Boolean?=false,
    var smallImageUrl:String?=""
) : Parcelable