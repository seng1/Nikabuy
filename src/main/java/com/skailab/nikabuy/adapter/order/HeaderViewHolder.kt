package com.skailab.nikabuy.adapter.order

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.skailab.nikabuy.App
import com.skailab.nikabuy.LocaleHelper
import com.skailab.nikabuy.R
import com.skailab.nikabuy.models.Order
import kotlinx.android.synthetic.main.section_order_header.view.*
import kotlinx.android.synthetic.main.section_order_item.view.txtTitle

class HeaderViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    fun setValue(order:Order){
        view.txtTitle.text=order.title
        view.txtDate.text=order.dateText
        view.txtTotal.text= App.resourses!!.getString(R.string.total_semi)+" "+ LocaleHelper.priceFormat(order.total)
        view.txtShipingFee.text=App.resourses!!.getString(R.string.shipping_fee_in_china_semi)+" "+LocaleHelper.priceFormat(order.auditShippingInChinaFeeInUsd)
        view.txtTranspoation.text=order.transportationMethodInCh!!.title
        view.chkProtected.isChecked=order.isProtected!!
        view.txtStatus.text=order.orderStatus!!.title
    }
}
