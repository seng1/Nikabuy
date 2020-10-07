package com.skailab.nikabuy.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Score (
    var deliveryScore:Double?=null,
    var itemScore:Double?=null,
    var serviceScore:Double?=null,
    var averageScore:Double?=null
) : Parcelable
@Parcelize
data class VendorRating (
    var level:Int?=null,
    var score:Int?=null,
    var totalFeedbacks:Int?=null,
    var positiveFeedbacks:Int?=null
) : Parcelable
@Parcelize
data class Location (
    var state:String?=""
) : Parcelable
@Parcelize
data class Vendor (
    var name:String?="",
    var shopName:String?="",
    var displayName:String?="",
    var email:String?="",
    var pictureUrl:String?="",
    var scores:Score?=null,
    var location: Location?=null,
    var credit:VendorRating?=null,
    var id:String?=""
) : Parcelable