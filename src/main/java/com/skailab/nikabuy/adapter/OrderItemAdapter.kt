package com.skailab.nikabuy.adapter

import com.skailab.nikabuy.databinding.ViewOrderItemBinding


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skailab.nikabuy.LocaleHelper
import com.skailab.nikabuy.models.Cart
import kotlinx.android.synthetic.main.view_order_item_detail.view.*

class OrderItemAdapter( val onClickListener: OrderItemAdapter.OnClickListener)
    : ListAdapter<Cart, OrderItemAdapter.OrderItemViewHolder>(DiffCallback) {
    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        val product = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(product)
        }
        holder.bind(product)
        holder.itemView.txtPrice.text=product.quantity!!.toString()+"*"+product.price!!.toString()+"="+LocaleHelper.priceFormat(product.quantity!!*product.price!!)
    }
    class OnClickListener(val clickListener: (product: Cart) -> Unit) {
        fun onClick(product: Cart) = clickListener(product)
    }
    companion object DiffCallback : DiffUtil.ItemCallback<Cart>() {
        override fun areItemsTheSame(oldItem: Cart, newItem: Cart): Boolean {
            return oldItem === newItem
        }
        override fun areContentsTheSame(oldItem: Cart, newItem: Cart): Boolean {
            return oldItem.itemId + oldItem.skuText == newItem.itemId+newItem.skuText
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        return OrderItemViewHolder(ViewOrderItemBinding.inflate(LayoutInflater.from(parent.context)))
    }
    class OrderItemViewHolder(private var binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel: Cart) {
            var alibaViewBinding = binding as ViewOrderItemBinding
            alibaViewBinding.viewModel = viewModel
            alibaViewBinding.executePendingBindings()
        }
    }
}