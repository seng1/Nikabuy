package com.skailab.nikabuy.models


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
open class MulitpleOrderPayment(
    var buyerId:Int?=0,
    var password:String?="",
    var orders:List<Order>?=null
)