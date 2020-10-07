package com.skailab.nikabuy.adapter.order

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.skailab.nikabuy.models.Order
import kotlinx.android.synthetic.main.section_order_footer.view.*
import kotlinx.android.synthetic.main.section_order_header.view.*
import kotlinx.android.synthetic.main.section_order_item.view.*

class FooterViewHolder(val rootView: View) : RecyclerView.ViewHolder(rootView){
    fun setValue(order: Order){
      if(!order.showCancelButton!!){
          rootView.btnCancel.visibility=View.GONE
      }
        if(!order.showPayButton!!){
            rootView.btnPay.visibility=View.GONE
        }

    }
}
