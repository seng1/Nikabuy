package com.skailab.nikabuy.models

import com.skailab.nikabuy.adapter.order.FooterViewHolder
import com.skailab.nikabuy.adapter.order.HeaderViewHolder

open class Order {
    var id:Int?=0
    var title:String?=""
    var isProtected:Boolean?=false
    var printLabel:String?=""
    var total:Double?=0.0
    var auditShippingInChinaFeeInUsd:Double?=0.0
    var transportationMethodInCh:Lookup?=null
    var purchaseOrderItems:List<Cart>?=null
    var dateText:String?=""
    var orderStatus:Lookup?=null
    var showPayButton:Boolean?=false
    var showCancelButton:Boolean?=false

}