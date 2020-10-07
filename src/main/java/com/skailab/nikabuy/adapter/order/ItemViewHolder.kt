package com.skailab.nikabuy.adapter.order

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.skailab.nikabuy.App
import com.skailab.nikabuy.LocaleHelper
import com.skailab.nikabuy.R
import com.skailab.nikabuy.models.Cart
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.section_order_item.view.*

class ItemViewHolder(val rootView: View) :
    RecyclerView.ViewHolder(rootView) {
    fun setValue(cart:Cart){
        Picasso.get().load(cart.imageUrl).placeholder(App.resourses!!.getDrawable(R.drawable.no_image,null)).into(rootView.imgItem)
        rootView.txtTitle.text=cart.title
        rootView.txtSku.text=cart.skuText
        rootView.txtPrice.text=cart.quantity!!.toString()+"*"+cart.price!!.toString()+"="+LocaleHelper.priceFormat(cart.quantity!!*cart.price!!)
    }
}