package com.skailab.nikabuy.adapter.orderSubmit

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.skailab.nikabuy.App
import com.skailab.nikabuy.R
import com.skailab.nikabuy.models.ShopCart
import kotlinx.android.synthetic.main.section__order_submit_header.view.*

class HeaderViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    fun setValue(shop: ShopCart){
        view.txtShop.text= App.resourses!!.getString(R.string.shop)+": "+shop.shopName
    }
}
