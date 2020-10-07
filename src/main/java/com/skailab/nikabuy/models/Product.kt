package com.skailab.nikabuy.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Product (
    var title:String="",
    var quantity:Int=0,
    var unitPrice:Double=0.0,
    var unitPriceInChn:Double=0.0,
    var subTotal:Double=0.0,
    var sourceUrl:String="",
    var imageUrl:String="",
    var imageUrls:Array<String>?=null,
    var itemId:String="",
    var exchangeRate:Double=0.0,
    var shopId:String?="",
    var shopUrl:String?="",
    var quantitySoldText:String?="",
    var isLoading:Boolean?=false,
    var isVisible:Boolean?=false,
    var originalTitle:String?="",
    var discountPriceRangetext:String?="",
    var orginalPriceRangetext:String?=""
): Parcelable