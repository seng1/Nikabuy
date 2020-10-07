package com.skailab.nikabuy.models.filter

open class OtFilter :Filter(){
    var provider:String="Taobao"
    var itemTitle:String=""
    var minPrice:Double?=null
    var maxPrice:Double?=null
    var imageUrl:String=""
    var baseString:String=""
    var vendorId:String?=""
    var itemId:String=""
    var brandId:String?=""
    var categoryId:String?=""
}