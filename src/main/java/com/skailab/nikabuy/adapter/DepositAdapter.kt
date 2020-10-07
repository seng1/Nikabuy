package com.skailab.nikabuy.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skailab.nikabuy.databinding.ViewDepositItemBinding
import com.skailab.nikabuy.models.BankAccount
import com.skailab.nikabuy.models.Deposit
import com.skailab.nikabuy.models.DepositAccount

class DepositAdapter( val onClickListener: OnClickListener)
    : ListAdapter<Deposit, DepositAdapter.DepositItemViewHolder>(DiffCallback) {
    override fun onBindViewHolder(holder: DepositItemViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        val product = getItem(position)
        if(product.depositAccount==null){
            product.depositAccount= DepositAccount()
            product.depositAccount!!.bankAccount= BankAccount()
        }

        holder.itemView.setOnClickListener {
            onClickListener.onClick(product)
        }
        holder.bind(product)
    }
    class OnClickListener(val clickListener: (product: Deposit) -> Unit) {
        fun onClick(product: Deposit) = clickListener(product)
    }
    companion object DiffCallback : DiffUtil.ItemCallback<Deposit>() {
        override fun areItemsTheSame(oldItem: Deposit, newItem: Deposit): Boolean {
            return oldItem === newItem
        }
        override fun areContentsTheSame(oldItem: Deposit, newItem: Deposit): Boolean {
            return oldItem.referenceNumber == newItem.referenceNumber
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DepositItemViewHolder {
        return DepositItemViewHolder(ViewDepositItemBinding.inflate(LayoutInflater.from(parent.context)))
    }
    class DepositItemViewHolder(private var binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel: Deposit) {
            var alibaViewBinding = binding as ViewDepositItemBinding
            alibaViewBinding.viewModel = viewModel
            alibaViewBinding.executePendingBindings()
        }
    }
}