package com.skailab.nikabuy.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class OrderSubmit(
    var id:Int?=null,
    var title:String?="",
    var buyerId:Int?=null,
    var Buyer:Buyer?=null,
    var transportationMethodInChId:Int?=null,
    var transportationMethodInCh:Lookup?=null,
    var transportationMethodInKhId:Int?=0,
    var transportationMethodInKh:Lookup?=null,
    var warehouseId:Int?=0,
    var warehouse:Lookup?=null,
    var isProtected:Boolean?=false,
    var note:String?="",
    var paymentMethodId:Int?=0,
    var paymentMethod:Lookup?=null,
    var printLabel:String?="",
    var serviceChargeRate:Double?=0.0,
    var exchangeRate:Double?=0.0,
    var purchaseOrderItems:List<Cart>?=null,
    var shopCarts:MutableList<ShopCart>?=null,
    var transpotationChs:List<Lookup>?=null,
    var paymentMethods:List<Lookup>?=null,
    var contacts:List<Contact>?=null,
    var transpotationKhs:List<Lookup>?=null,
    var warehouses:List<Lookup>?=null,
    var total:Double?=0.0,
    var purchaseOrderContact:Contact?=null
): Parcelable