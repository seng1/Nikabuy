package com.skailab.nikabuy.adapter

import android.graphics.Color
import android.graphics.Color.YELLOW
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skailab.nikabuy.App.Companion.resourses
import com.skailab.nikabuy.R
import com.skailab.nikabuy.databinding.View1688MainSkuBinding
import com.skailab.nikabuy.databinding.ViewProductTaobaoSkuBinding
import com.skailab.nikabuy.models.Sku1688
import kotlinx.android.synthetic.main.view_product_sku_item.view.icon_image
import kotlinx.android.synthetic.main.view_product_taobao_sku.view.*

class Product1688MainSkuAdapter( val onClickListener: OnClickListener) : ListAdapter<Sku1688, Product1688MainSkuAdapter.ViewHolder>(DiffCallback) {

    class OnClickListener(val clickListener: (product: Sku1688) -> Unit) {
        fun onClick(product:Sku1688) = clickListener(product)

    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(product)
        }
        if(product.isHasImage==false){
            holder.itemView.icon_image.visibility= View.GONE
        }
        if(product.isSelected !=null && product.isSelected!!){
            holder.itemView.productContent.setBackgroundColor(resourses!!.getColor(R.color.colorPrimary,null))
        }
        else{
            holder.itemView.productContent.setBackgroundColor(Color.WHITE)
        }
        holder.bind(product)
    }
    companion object DiffCallback : DiffUtil.ItemCallback<Sku1688>() {
        override fun areItemsTheSame(oldItem: Sku1688, newItem: Sku1688): Boolean {
            return oldItem === newItem
        }
        override fun areContentsTheSame(oldItem: Sku1688, newItem: Sku1688): Boolean {
            return oldItem.orignalSkuText == newItem.orignalSkuText
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(View1688MainSkuBinding.inflate(LayoutInflater.from(parent.context)))
    }
    class ViewHolder(private var binding: ViewDataBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel: Sku1688) {
            var alibaViewBinding =binding as View1688MainSkuBinding
            alibaViewBinding.viewModel = viewModel
            alibaViewBinding.executePendingBindings()


        }

    }

}