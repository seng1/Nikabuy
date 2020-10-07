package com.skailab.nikabuy.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skailab.nikabuy.databinding.ViewBoxItemBinding
import com.skailab.nikabuy.models.Box
import com.skailab.nikabuy.viewModels.ProductSkuItemViewModel
import kotlinx.android.synthetic.main.section_order_footer.view.*

class BoxAdapter( val onClickListener: OnClickListener)
    : ListAdapter<Box, BoxAdapter.BoxItemViewHolder>(DiffCallback) {
    override fun onBindViewHolder(holder: BoxItemViewHolder, position: Int) {
        val product = getItem(position)
        holder.itemView.btnPay.setOnClickListener {
            onClickListener.onClick(product,holder)
        }
        holder.bind(product)
    }
    class OnClickListener(val clickListener: (box: Box, boxItemViewHolder: BoxItemViewHolder) -> Unit) {
        fun onClick(box: Box, boxItemViewHolder: BoxItemViewHolder) = clickListener(box, boxItemViewHolder)

    }
    companion object DiffCallback : DiffUtil.ItemCallback<Box>() {
        override fun areItemsTheSame(oldItem: Box, newItem: Box): Boolean {
            return oldItem === newItem
        }
        override fun areContentsTheSame(oldItem: Box, newItem: Box): Boolean {
            return oldItem.id == newItem.id
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoxItemViewHolder {
        return BoxItemViewHolder(ViewBoxItemBinding.inflate(LayoutInflater.from(parent.context)))
    }
    class BoxItemViewHolder(private var binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel: Box) {
            var alibaViewBinding = binding as ViewBoxItemBinding
            alibaViewBinding.viewModel = viewModel
            alibaViewBinding.executePendingBindings()
        }
    }
}