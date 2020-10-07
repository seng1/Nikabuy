package com.skailab.nikabuy.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Region(
    var id:Int?=null,
    var title:String?="",
    var level:Int?=null,
    var regionCode:Int?=null,
    var parentId:Int?=null
) : Parcelable