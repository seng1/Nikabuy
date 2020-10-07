package com.skailab.nikabuy.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data  class ProductCartParcelize(
    var buyerId:Int=0,
    var messageToCustomerService:String="",
    var product:ProductDetail?=null,
    var carts:List<Cart>?=null,
    var language:String?=""
): Parcelable