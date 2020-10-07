package com.skailab.nikabuy.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skailab.nikabuy.databinding.ViewCartItemBinding
import com.skailab.nikabuy.databinding.ViewProductSkuItemBinding
import com.skailab.nikabuy.models.Cart
import com.skailab.nikabuy.viewModels.ProductSkuItemViewModel
import kotlinx.android.synthetic.main.view_cart_item.view.*
import kotlinx.android.synthetic.main.view_product_sku_item.view.*
import kotlinx.android.synthetic.main.view_product_sku_item.view.icon_image

class CartAdapter( val onClickListener: OnClickListener,
                   val onRemoveChangeListener: OnClickListener,
                   val onImageListener:OnClickListener,
                   val onEditListener:OnClickListener,
                   val onSelectListener:OnClickListener
                   )
    : ListAdapter<Cart, CartAdapter.CartViewHolder>(DiffCallback) {
    class OnClickListener(val clickListener: (product: Cart) -> Unit) {
        fun onClick(product: Cart) = clickListener(product)
    }
    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val product = getItem(position)
        holder.itemView.icon_image.setOnClickListener({
            onImageListener.onClick(product)
        })
        holder.itemView.txtDetail.setOnClickListener({
            onClickListener.onClick(product)
        })
        holder.itemView.btnDelete.setOnClickListener {
            onRemoveChangeListener.onClick(product)
        }
        holder.itemView.btnEdit.setOnClickListener({
            onEditListener.onClick(product)
        })
        holder.itemView.rndSelect.setOnClickListener({
            product.isSelected=holder.itemView.rndSelect.isChecked
            onSelectListener.onClick(product)
        })
        holder.bind(product)
    }
    companion object DiffCallback : DiffUtil.ItemCallback<Cart>() {
        override fun areItemsTheSame(oldItem: Cart, newItem: Cart): Boolean {
            return oldItem === newItem
        }
        override fun areContentsTheSame(oldItem: Cart, newItem: Cart): Boolean {
            return oldItem.itemId + oldItem.skuText == newItem.itemId+newItem.skuText
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        return CartViewHolder(ViewCartItemBinding.inflate(LayoutInflater.from(parent.context)))
    }
    class CartViewHolder(private var binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel: Cart) {
            var alibaViewBinding = binding as ViewCartItemBinding
            alibaViewBinding.viewModel = viewModel
            alibaViewBinding.executePendingBindings()
        }
    }
}