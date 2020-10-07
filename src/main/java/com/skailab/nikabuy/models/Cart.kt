package com.skailab.nikabuy.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Cart(
    var id:Int?=null,
    var itemId:String?="",
    var buyerId:Int?=null,
    var orignalTitle:String?="",
    var title:String?="",
    var quantity:Int?=null,
    var description:String?="",
    var imageUrl:String?="",
    var userImageUrl:String?="",
    var skuText:String?="",
    var unitPriceInChn:Double?=null,
    var shopId:String?="",
    var shopUrl:String?="",
    var shopName:String?="",
    var brandId:String?="",
    var categoryId:String?="",
    var price:Double?=null,
    var priceRanges:List<PriceRange>?=null,
    var isSelected:Boolean?=false,
    var originalSkuText:String?=""
) : Parcelable

@Parcelize
data class ShopCart(
    var shopId:String?="",
    var shopName:String?="",
    var carts:MutableList<Cart>?=null,
    var isSelected: Boolean?=false
) : Parcelable