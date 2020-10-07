package com.skailab.nikabuy.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skailab.nikabuy.LocaleHelper
import com.skailab.nikabuy.databinding.View1688SkuItemBinding
import com.skailab.nikabuy.models.SkuItem1688Rvc
import kotlinx.android.synthetic.main.view_1688_sku_item.view.*
import kotlinx.android.synthetic.main.view_product_sku_item.view.icon_image


class Product1688SkuItemAdapter( val onClickListener: OnClickListener,val onQtyChangeListener: OnQtyChangeListener) : ListAdapter<SkuItem1688Rvc, Product1688SkuItemAdapter.ViewHolder>(DiffCallback) {

    class OnClickListener(val clickListener: (product: SkuItem1688Rvc) -> Unit) {
        fun onClick(product:SkuItem1688Rvc) = clickListener(product)
    }
    class OnQtyChangeListener(val clickListener: (product: SkuItem1688Rvc) -> Unit) {
        fun onClick(product:SkuItem1688Rvc) = clickListener(product)

    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val product = getItem(position)
        product.TextViewPrice=holder.itemView.textViewPrice

        holder.itemView.icon_image.setOnClickListener {
            onClickListener.onClick(product)
        }
        holder.itemView.plusBtn.setOnClickListener {
            product.sku.quantity=product.sku.quantity!!+1
            holder.itemView.quantity.setText(product.sku.quantity.toString())
            onQtyChangeListener.onClick(product)
        }
        holder.itemView.minusBtn.setOnClickListener {
            if(product.sku.quantity!!>0){
                product.sku.quantity=product.sku.quantity!!-1
                holder.itemView.quantity.setText(product.sku.quantity.toString())
                onQtyChangeListener.onClick(product)
            }
        }
        holder.itemView.quantity.addTextChangedListener(object:
            TextWatcher {override fun afterTextChanged(s: Editable?) {
        }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(holder.itemView.quantity.text.isNullOrEmpty()){
                    product.sku.quantity=0
                    onQtyChangeListener.onClick(product)
                }
                else{
                    product.sku.quantity=holder.itemView.quantity.text.toString().toInt()
                    onQtyChangeListener.onClick(product)
                }
            }
        })
        if(product.sku.isHasImage==false){
            holder.itemView.icon_image.visibility= View.GONE
        }
        var skuScreenWidth=LocaleHelper.getScreenWidthInDp()-420
        if(product.sku.isHasImage==true){
            skuScreenWidth=skuScreenWidth-200
        }
        val layoutParams = LinearLayout.LayoutParams(skuScreenWidth, LinearLayout.LayoutParams.WRAP_CONTENT)
        holder.itemView.textViewProductName.layoutParams =layoutParams
        holder.itemView.quantity.setText(product.sku.quantity.toString())
        holder.bind(product)
    }
    companion object DiffCallback : DiffUtil.ItemCallback<SkuItem1688Rvc>() {
        override fun areItemsTheSame(oldItem: SkuItem1688Rvc, newItem: SkuItem1688Rvc): Boolean {
            return oldItem === newItem
        }
        override fun areContentsTheSame(oldItem: SkuItem1688Rvc, newItem: SkuItem1688Rvc): Boolean {
            return oldItem.sku.orignalSkuText == newItem.sku.orignalSkuText
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(View1688SkuItemBinding.inflate(LayoutInflater.from(parent.context)))
    }
    class ViewHolder(private var binding: ViewDataBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel: SkuItem1688Rvc) {
            var alibaViewBinding =binding as View1688SkuItemBinding
            alibaViewBinding.viewModel = viewModel
            alibaViewBinding.executePendingBindings()
        }

    }

}