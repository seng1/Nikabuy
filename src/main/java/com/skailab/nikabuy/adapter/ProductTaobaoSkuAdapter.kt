package com.skailab.nikabuy.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skailab.nikabuy.App.Companion.resourses
import com.skailab.nikabuy.R
import com.skailab.nikabuy.databinding.ViewProductTaobaoSkuBinding
import com.skailab.nikabuy.models.SkuItemModel
import kotlinx.android.synthetic.main.view_product_sku_item.view.icon_image
import kotlinx.android.synthetic.main.view_product_taobao_sku.view.*

class ProductTaobaoSkuAdapter( val onClickListener: OnClickListener) : ListAdapter<SkuItemModel, ProductTaobaoSkuAdapter.ViewHolder>(DiffCallback) {

    class OnClickListener(val clickListener: (product: SkuItemModel,adapter: ProductTaobaoSkuAdapter) -> Unit) {
        fun onClick(product:SkuItemModel,adapter: ProductTaobaoSkuAdapter) = clickListener(product,adapter)

    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(product,this)
        }
        if(product.isHasImage==false){
            holder.itemView.icon_image.visibility= View.GONE
        }
        if(product.isSelected==false){
            holder.itemView.productContent.setBackgroundColor(Color.WHITE)
        }
        else{
            holder.itemView.productContent.setBackgroundColor(resourses!!.getColor(R.color.colorPrimary,null))
        }
        holder.bind(product)
    }
    companion object DiffCallback : DiffUtil.ItemCallback<SkuItemModel>() {
        override fun areItemsTheSame(oldItem: SkuItemModel, newItem: SkuItemModel): Boolean {
            return oldItem === newItem
        }
        override fun areContentsTheSame(oldItem: SkuItemModel, newItem: SkuItemModel): Boolean {
            return oldItem.orignalTitle == newItem.orignalTitle
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ViewProductTaobaoSkuBinding.inflate(LayoutInflater.from(parent.context)))
    }
    class ViewHolder(private var binding: ViewDataBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel: SkuItemModel) {
            var alibaViewBinding =binding as ViewProductTaobaoSkuBinding
            alibaViewBinding.viewModel = viewModel
            alibaViewBinding.executePendingBindings()


        }

    }

}