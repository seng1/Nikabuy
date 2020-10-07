package com.skailab.nikabuy.adapter.cart

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.skailab.nikabuy.App
import com.skailab.nikabuy.R
import com.skailab.nikabuy.models.ShopCart
import kotlinx.android.synthetic.main.section_cart_header.view.*
import kotlinx.android.synthetic.main.view_cart_item.view.*


class HeaderViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    fun setValue(shop: ShopCart){
        view.txtShop.text= App.resourses!!.getString(R.string.shop)+": "+shop.shopName
        if(shop.isSelected!!){
            view.chkCheck.isChecked=true
        }
        else{
            view.chkCheck.isChecked= false
        }
        if(shop.carts!!.count()==0){
            view.visibility=View.GONE
        }
    }
}
