package com.skailab.nikabuy.adapter.orderSubmit

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.skailab.nikabuy.R
import com.skailab.nikabuy.adapter.cart.CartSection
import com.skailab.nikabuy.models.Cart
import com.skailab.nikabuy.models.ShopCart
import io.github.luizgrp.sectionedrecyclerviewadapter.Section
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters
import kotlinx.android.synthetic.main.section__order_submit_item.view.*

class OrderSubmitSection(private val shop: ShopCart, val onItemCheckChange: OrderSubmitSection.OnItemClickListener) : Section(
    SectionParameters.builder()
        .itemResourceId(R.layout.section__order_submit_item)
        .headerResourceId(R.layout.section__order_submit_header)
        .build()
) {
    override fun getContentItemsTotal(): Int {
        return shop.carts!!.size
    }
    override fun getItemViewHolder(view: View): RecyclerView.ViewHolder {
        return ItemViewHolder(view)
    }
    class OnItemClickListener(val clickListener: (cart: Cart) -> Unit) {
        fun onClick(cart: Cart) = clickListener(cart)
    }
    override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemHolder = holder as ItemViewHolder
        itemHolder.setIsRecyclable(false)
        val cart=shop.carts!![position]
        itemHolder.setValue(cart)
        itemHolder.rootView.img.setOnClickListener{
            onItemCheckChange.onClick(cart)
        }
    }
    override fun getHeaderViewHolder(view: View): RecyclerView.ViewHolder {
        return HeaderViewHolder(view)
    }

    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder) {
        val headerHolder = holder as HeaderViewHolder
        headerHolder.setIsRecyclable(false)
        headerHolder.setValue(shop)
    }
}