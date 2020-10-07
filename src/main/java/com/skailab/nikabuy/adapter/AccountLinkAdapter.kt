package com.skailab.nikabuy.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skailab.nikabuy.databinding.ViewAccountLinkBinding
import com.skailab.nikabuy.models.User

class AccountLinkAdapter()
    : ListAdapter<User, AccountLinkAdapter.AccountLinkItemViewHolder>(DiffCallback) {
    override fun onBindViewHolder(holder: AccountLinkItemViewHolder, position: Int) {
        val product = getItem(position)
        holder.bind(product)
    }
    class OnClickListener(val clickListener: (box: User, boxItemViewHolder: AccountLinkItemViewHolder) -> Unit) {
        fun onClick(box: User, boxItemViewHolder: AccountLinkItemViewHolder) = clickListener(box, boxItemViewHolder)

    }
    companion object DiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem === newItem
        }
        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountLinkItemViewHolder {
        return AccountLinkItemViewHolder(ViewAccountLinkBinding.inflate(LayoutInflater.from(parent.context)))
    }
    class AccountLinkItemViewHolder(private var binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel: User) {
            var alibaViewBinding = binding as ViewAccountLinkBinding
            alibaViewBinding.viewModel = viewModel
            alibaViewBinding.executePendingBindings()
        }
    }
}