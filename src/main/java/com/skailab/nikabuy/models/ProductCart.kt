package com.skailab.nikabuy.models

open  class ProductCart(
    var buyerId:Int=0,
    var messageToCustomerService:String="",
    var product:ProductDetail?=null,
    var carts:List<Cart>?=null
)