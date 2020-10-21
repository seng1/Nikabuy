package com.skailab.nikabuy.adapter.cart

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.skailab.nikabuy.App
import com.skailab.nikabuy.LocaleHelper
import com.skailab.nikabuy.R
import com.skailab.nikabuy.models.Cart
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.section_cart_item.view.*
import kotlinx.android.synthetic.main.view_cart_item.view.icon_image
import kotlinx.android.synthetic.main.view_cart_item.view.txtDetail


class ItemViewHolder(val rootView: View) :
    RecyclerView.ViewHolder(rootView) {
    fun setValue(cart: Cart){
        Picasso.get().load(cart.imageUrl).placeholder(App.resourses!!.getDrawable(R.drawable.no_image,null)).into(rootView.img)
        rootView.txtDetail.text=cart.title
        rootView.txtSku.text=cart.skuText
        rootView.quantity.setText(cart.quantity.toString())
        rootView.messageBtn.visibility=View.GONE
        rootView.messageBtn.visibility=View.VISIBLE
        if(cart.isSelected!!){
            rootView.chkCheck.isChecked=true
        }
        else{
            rootView.chkCheck.isChecked=false
        }
        setPrice(cart)
    }
    fun setPrice(cart: Cart){
        rootView.textViewPrice.text="*" + cart.price.toString()+"="+LocaleHelper.priceFormat (cart.quantity!!*cart.price!!)
    }
}