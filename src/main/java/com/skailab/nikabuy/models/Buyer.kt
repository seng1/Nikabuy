package com.skailab.nikabuy.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
class Buyer (
    var email:String?="",
    var telephone:String?="",
    var code:String?="",
    var customerLevelId:Int?=null,
    var saleManId:Int?=null,
    var customerLevel:CustomerLevel?=null,
    var exchangeRate:Double=0.0,
    var  saleMan:SaleMan?=null,
    var id:Int=0,
    var name:String?="",
    var paymentPasswordCreated:Boolean?=null
    ): Parcelable