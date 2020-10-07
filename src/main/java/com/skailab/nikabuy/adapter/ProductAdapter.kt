package com.skailab.nikabuy.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skailab.nikabuy.databinding.ViewProductItemBinding
import com.skailab.nikabuy.databinding.ViewProductRowLoadingBinding
import com.skailab.nikabuy.models.Product

class ProductAdapter( val onClickListener: OnClickListener) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(DiffCallback) {
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
        return if (currentProduct.isLoading==null||!currentProduct.isLoading!!) {
            PRODUCT_ITEM

        } else {
            LOADING_ITEM
        }
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
        if(viewType==LOADING_ITEM) {
            return ProductViewHolder(ViewProductRowLoadingBinding.inflate(LayoutInflater.from(parent.context)))
        }
        else{
                return ProductViewHolder(ViewProductItemBinding.inflate(LayoutInflater.from(parent.context)))
        }


    }
    class ProductViewHolder(private var binding: ViewDataBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel: Product) {
            if(viewModel.isLoading==null || !viewModel.isLoading!!){
                var alibaViewBinding =binding as ViewProductItemBinding
                alibaViewBinding.viewModel = viewModel
                alibaViewBinding.executePendingBindings()
            }
            else{
                var alibaViewBinding =binding as ViewProductRowLoadingBinding
                alibaViewBinding.viewModel = viewModel
                alibaViewBinding.executePendingBindings()
            }

        }

    }

}