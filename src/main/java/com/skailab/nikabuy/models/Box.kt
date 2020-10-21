package com.skailab.nikabuy.models

class Box {
    var id:Int=0
    var title:String?=""
    var passcode:String?=""
    var shippingFee:Double?=0.0
    var isPaid:Boolean?=false
    var purchaseOrderNums:String?=""
    var dateText:String?=""
    var paymentDateText:String?=""
}
class  MultiplePayBox(
    var password:String?="",
    var buyerId:Int?=null,
    var boxes:List<Box>?=null
)