package com.skailab.nikabuy.models

import com.skailab.nikabuy.adapter.cart.HeaderViewHolder

open class ShopCartForBindingList {
    var shop:ShopCart?=null
    var headerViewHolder: HeaderViewHolder?=null
    constructor(shop: ShopCart){
        this.shop=shop
    }
}