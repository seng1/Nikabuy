package com.skailab.nikabuy.models

open class OrderDetail {
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
    var extraCharge:Double?=0.0
    var insureFeeCharge:Double?=0.0
    var productTotal:Double?=0.0
    var serviceChargeRate:Double?=0.0
    var serviceCharge:Double?=0.0
    var purchaseOrderContact:Contact?=null
    var showPayButton:Boolean?=false
}