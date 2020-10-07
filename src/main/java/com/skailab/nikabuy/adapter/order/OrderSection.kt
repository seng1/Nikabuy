package com.skailab.nikabuy.adapter.order

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.skailab.nikabuy.R
import com.skailab.nikabuy.models.Order
import com.skailab.nikabuy.models.OrderForBindingList
import com.squareup.picasso.Picasso
import io.github.luizgrp.sectionedrecyclerviewadapter.Section
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters
import kotlinx.android.synthetic.main.section_order_footer.view.*

class OrderSection(private val order: OrderForBindingList,
                   val onHeaderClick: OnClickListener,
                   val onItemClick: OnClickListener,
                   val onPrintRemarkClick: OnClickListener,
                   val onCancelClick: OnClickListener,
                   val onReOrderClick: OnClickListener,
                   val onPayClick: OnClickListener) : Section(
                SectionParameters.builder()
            .itemResourceId(R.layout.section_order_item)
            .headerResourceId(R.layout.section_order_header)
            .footerResourceId(R.layout.section_order_footer)
            .build()
    ) {
    override fun getContentItemsTotal(): Int {
        return order.order!!.purchaseOrderItems!!.size
    }
    class OnClickListener(val clickListener: (order: OrderForBindingList) -> Unit) {
        fun onClick(order: OrderForBindingList) = clickListener(order)
    }
    override fun getItemViewHolder(view: View): RecyclerView.ViewHolder {
        return ItemViewHolder(view)
    }

    override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemHolder = holder as ItemViewHolder
        itemHolder.setIsRecyclable(false)
        val cart=order.order!!.purchaseOrderItems!![position]
        itemHolder.setValue(cart)

        itemHolder.rootView.setOnClickListener({
            onItemClick.onClick(order)
        })
    }

    override fun getHeaderViewHolder(view: View): RecyclerView.ViewHolder {
        return HeaderViewHolder(view)
    }

    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder) {
        val headerHolder = holder as HeaderViewHolder
        headerHolder.setIsRecyclable(false)
        headerHolder.setValue(order.order!!)
        headerHolder.itemView.setOnClickListener({
            onHeaderClick.onClick(order)
        })
        order.headerViewHolder=headerHolder
    }

    override fun getFooterViewHolder(view: View): RecyclerView.ViewHolder {
        return FooterViewHolder(view)
    }

    override fun onBindFooterViewHolder(holder: RecyclerView.ViewHolder) {
        val footerHolder: FooterViewHolder = holder as FooterViewHolder
        footerHolder.setIsRecyclable(false)
        footerHolder.setValue(order.order!!)
        footerHolder.rootView.btnRemark.setOnClickListener({
            onPrintRemarkClick.onClick(order)
        })
        footerHolder.rootView.btnCancel.setOnClickListener({
            onCancelClick.onClick(order)
        })
        footerHolder.rootView.btnReorder.setOnClickListener({
            onReOrderClick.onClick(order)
        })
        footerHolder.rootView.btnPay.setOnClickListener({
            onPayClick.onClick(order)
        })
        order.footerViewHolder=footerHolder
    }
}
