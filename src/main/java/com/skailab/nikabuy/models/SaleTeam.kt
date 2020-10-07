package com.skailab.nikabuy.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class SaleTeam (
    var id:Int=0,
    var name:String="",
    var code:String=""
): Parcelable