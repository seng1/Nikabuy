package com.skailab.nikabuy.models

import com.skailab.nikabuy.adapter.order.FooterViewHolder
import com.skailab.nikabuy.adapter.order.HeaderViewHolder

open class OrderForBindingList {
   var order:Order?=null
    var footerViewHolder: FooterViewHolder?=null
    var headerViewHolder: HeaderViewHolder?=null
    constructor(order: Order){
        this.order=order
    }
}