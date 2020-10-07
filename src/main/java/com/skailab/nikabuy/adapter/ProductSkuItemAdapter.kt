package com.skailab.nikabuy.adapter


import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skailab.nikabuy.databinding.ViewProductSkuItemBinding
import com.skailab.nikabuy.viewModels.ProductSkuItemViewModel
import kotlinx.android.synthetic.main.view_product_sku_item.view.*

class ProductSkuItemAdapter( val onImageClickListener: OnImageClickListener,val onTextChangeListener: OnTextChangeListener) : ListAdapter<ProductSkuItemViewModel, ProductSkuItemAdapter.ProductSkuItemViewHolder>(DiffCallback) {
    class OnImageClickListener(val clickListener: (product: ProductSkuItemViewModel) -> Unit) {
        fun onClick(product: ProductSkuItemViewModel) = clickListener(product)
    }
    class OnTextChangeListener(val clickListener: (product: ProductSkuItemViewModel, index: Int) -> Unit) {
        fun onClick(product: ProductSkuItemViewModel, index: Int) = clickListener(product, index)
    }
    override fun onBindViewHolder(holder: ProductSkuItemViewHolder, position: Int) {
        val product = getItem(position)
        holder.setIsRecyclable(false)
        product.setHolder(holder)
        holder.itemView.icon_image.setOnClickListener({
            onImageClickListener.onClick(product)
        })
        holder.itemView.quantity.addTextChangedListener(object:
            TextWatcher {override fun afterTextChanged(s: Editable?) {

        }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (product.product.value!!.quantity.toString() != holder.itemView.quantity.toString()) {
                    if (!holder.itemView.quantity.text.isNullOrEmpty()) {
                        product.product.value!!.quantity = holder.itemView.quantity.text.toString().toInt()
                        onTextChangeListener.onClick(product, position)
                    } else {
                        product.product.value!!.quantity= null
                    }
                    onTextChangeListener.onClick(product, position)
                }
            }
        })
        holder.bind(product)
    }
    companion object DiffCallback : DiffUtil.ItemCallback<ProductSkuItemViewModel>() {
        override fun areItemsTheSame(oldItem: ProductSkuItemViewModel, newItem: ProductSkuItemViewModel): Boolean {
            return oldItem === newItem
        }
        override fun areContentsTheSame(oldItem: ProductSkuItemViewModel, newItem: ProductSkuItemViewModel): Boolean {
            return oldItem.product.value!!.orignalSkuText == newItem.product.value!!.orignalSkuText
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductSkuItemViewHolder {
        val binding = ProductSkuItemViewHolder(ViewProductSkuItemBinding.inflate(LayoutInflater.from(parent.context)))
        return binding
    }
    class ProductSkuItemViewHolder(private var binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel: ProductSkuItemViewModel) {
            var alibaViewBinding = binding as ViewProductSkuItemBinding
            alibaViewBinding.viewModel = viewModel
            alibaViewBinding.executePendingBindings()
        }
    }
}