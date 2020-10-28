package com.skailab.nikabuy.adapter.cart

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.skailab.nikabuy.R
import com.skailab.nikabuy.models.Cart
import com.skailab.nikabuy.models.ShopCartForBindingList
import io.github.luizgrp.sectionedrecyclerviewadapter.Section
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters
import kotlinx.android.synthetic.main.section_cart_item.view.*

class CartSection(private val shop: ShopCartForBindingList,
                  val onHeaderCheckChange: OnClickListener,
                  val onItemCheckChange:OnItemClickListener,
                  val onImageClick:OnItemClickListener,
                  val onEditClick:OnItemClickListener,
                  val onDeleteClick:OnItemClickListener) : Section(
    SectionParameters.builder()
        .itemResourceId(R.layout.section_cart_item)
        .headerResourceId(R.layout.section_cart_header)
        .build()
) {
    override fun getContentItemsTotal(): Int {
        return shop.shop!!.carts!!.size
    }
    override fun getItemViewHolder(view: View): RecyclerView.ViewHolder {
        return ItemViewHolder(view)
    }
    class OnClickListener(val clickListener: (shop: ShopCartForBindingList) -> Unit) {
        fun onClick(shop: ShopCartForBindingList) = clickListener(shop)
    }
    class OnItemClickListener(val clickListener: (cart: Cart) -> Unit) {
        fun onClick(cart: Cart) = clickListener(cart)
    }
    override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemHolder = holder as ItemViewHolder
        itemHolder.setIsRecyclable(false)
        val cart=shop.shop!!.carts!![position]
        itemHolder.setValue(cart)
        itemHolder.rootView.chkCheck.setOnClickListener {
            if(cart.isSelected!!){
                cart.isSelected=false
            }
            else{
                cart.isSelected=true
            }
            onItemCheckChange.onClick(cart)
        }
        itemHolder.rootView.plusBtn.setOnClickListener {
            cart.quantity=cart.quantity!!+1
            itemHolder.rootView.quantity.setText(cart.quantity.toString())
            itemHolder.setPrice(cart)
            if(cart.isSelected!!){
                onItemCheckChange.onClick(cart)
            }
        }
        itemHolder.rootView.minusBtn.setOnClickListener {
           if(cart.quantity!!>0){
               cart.quantity=cart.quantity!!-1
               itemHolder.rootView.quantity.setText(cart.quantity.toString())
               itemHolder.setPrice(cart)
               if(cart.isSelected!!){
                   onItemCheckChange.onClick(cart)
               }
           }
        }
        itemHolder.rootView.quantity.addTextChangedListener(object:
            TextWatcher {override fun afterTextChanged(s: Editable?) {
        }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(itemHolder.rootView.quantity.text.isNullOrEmpty()==false)
                {
                    cart.quantity=itemHolder.rootView.quantity.text.toString().toInt()
                    itemHolder.setPrice(cart)
                    if(cart.isSelected!!){
                        onItemCheckChange.onClick(cart)
                    }
                }
            }
        })
        itemHolder.rootView.img.setOnClickListener {
            onImageClick.onClick(cart)
        }
        itemHolder.rootView.messageBtn.setOnClickListener {
            onEditClick.onClick(cart)
        }
        itemHolder.rootView.messageNoBtn.setOnClickListener {
            onEditClick.onClick(cart)
        }
        itemHolder.rootView.deleteBtn.setOnClickListener {
            onDeleteClick.onClick(cart)
        }
    }
    override fun getHeaderViewHolder(view: View): RecyclerView.ViewHolder {
        return HeaderViewHolder(view)
    }

    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder) {
        val headerHolder = holder as HeaderViewHolder
        headerHolder.setIsRecyclable(false)
        headerHolder.setValue(shop.shop!!)
        headerHolder.itemView.chkCheck.setOnClickListener{
            if(shop.shop!!.isSelected==false){
                shop.shop!!.isSelected=true
        }
            else{
                shop.shop!!.isSelected=false
            }
            onHeaderCheckChange.onClick(shop)
        }
        shop.headerViewHolder=headerHolder
    }
}
