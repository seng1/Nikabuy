package com.skailab.nikabuy.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Lookup(
    var id:Int?=null,
    var title:String?=""
): Parcelable