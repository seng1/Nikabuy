package com.skailab.nikabuy.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class CustomerLevel (
    var id:Int=0,
    var code:String="",
    var exchangeRate:Double =0.0,
    var serviceCharge:Double =0.0,
    var name:String?=""
    ): Parcelable