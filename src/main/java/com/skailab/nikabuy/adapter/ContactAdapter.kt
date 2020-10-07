package com.skailab.nikabuy.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skailab.nikabuy.databinding.ViewContactItemBinding
import com.skailab.nikabuy.models.Contact
import kotlinx.android.synthetic.main.view_cart_item.view.*
import kotlinx.android.synthetic.main.view_contact_item.view.*

class ContactAdapter( val onClickListener: OnClickListener, val onDeleteClickListener: OnDeleteClickListener)
    : ListAdapter<Contact, ContactAdapter.ContactItemViewHolder>(DiffCallback) {
    override fun onBindViewHolder(holder: ContactItemViewHolder, position: Int) {
        val product = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(product)
        }
        holder.itemView.btnDeleteContact.setOnClickListener({
            onDeleteClickListener.onClick(product,position)
        })
        holder.setIsRecyclable(false)
        holder.bind(product)
    }
    class OnClickListener(val clickListener: (product: Contact) -> Unit) {
        fun onClick(product: Contact) = clickListener(product)
    }
    class OnDeleteClickListener(val clickListener: (product: Contact,index:Int) -> Unit) {
        fun onClick(product: Contact,index:Int) = clickListener(product,index)
    }
    companion object DiffCallback : DiffUtil.ItemCallback<Contact>() {
        override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem === newItem
        }
        override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem.id == newItem.id
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactItemViewHolder {
        return ContactItemViewHolder(ViewContactItemBinding.inflate(LayoutInflater.from(parent.context)))
    }
    class ContactItemViewHolder(private var binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel: Contact) {
            var alibaViewBinding = binding as ViewContactItemBinding
            alibaViewBinding.viewModel = viewModel
            alibaViewBinding.executePendingBindings()
        }
    }
}