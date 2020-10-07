package com.skailab.nikabuy.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Contact(
    var id:Int?=null,
    var title:String?="",
    var contactNumber:String?="",
    var email:String?="",
    var mapUrl:String?="",
    var address:String?="",
    var isDefaultContact:Boolean?=false,
    var country:Region?=null,
    var province:Region?=null,
    var district:Region?=null,
    var displayText:String?=""
): Parcelable