package com.skailab.nikabuy.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skailab.nikabuy.databinding.ViewProductRecommendBinding
import com.skailab.nikabuy.models.Product

class ProductRecommentAdapter( val onClickListener: OnClickListener) : ListAdapter<Product, ProductRecommentAdapter.ProductViewHolder>(DiffCallback) {
    val LOADING_ITEM = 0
    val PRODUCT_ITEM = 1
    class OnClickListener(val clickListener: (product: Product) -> Unit) {
        fun onClick(product:Product) = clickListener(product)
    }
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(product)
        }
        holder.bind(product)

    }
    override fun getItemViewType(position: Int): Int {
        val currentProduct = getItem(position)
        return  PRODUCT_ITEM
    }
    companion object DiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem === newItem
        }
        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.itemId == newItem.itemId
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(ViewProductRecommendBinding.inflate(LayoutInflater.from(parent.context)))

    }
    class ProductViewHolder(private var binding: ViewDataBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel: Product) {
            var alibaViewBinding =binding as ViewProductRecommendBinding
            alibaViewBinding.viewModel = viewModel
            alibaViewBinding.executePendingBindings()

        }

    }

}