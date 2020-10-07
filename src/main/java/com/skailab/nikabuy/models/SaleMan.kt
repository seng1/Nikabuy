package com.skailab.nikabuy.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class SaleMan (
    var id:Int=0,
    var name:String="",
    var code:String="",
    var saleTeamId:Int=0,
    var saleTeam: SaleTeam? =null
    ) : Parcelable