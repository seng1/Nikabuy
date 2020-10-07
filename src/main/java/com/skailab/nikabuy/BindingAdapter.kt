package com.skailab.nikabuy


import android.graphics.Paint
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skailab.nikabuy.App.Companion.resourses
import com.skailab.nikabuy.adapter.*
import com.skailab.nikabuy.models.*
import com.skailab.nikabuy.viewModels.ProductSkuItemViewModel
import com.squareup.picasso.Picasso

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    if(!imgUrl.isNullOrEmpty()){
        Picasso.get().load(imgUrl).placeholder(resourses!!.getDrawable(R.drawable.no_image,null)).into(imgView)
    }
}
@BindingAdapter("Price")
fun bindPrice(textView: TextView, price: Double?) {
    if(price==null){
        textView.text=""
        return
    }
    textView.text=price.toString()+" $"
    //textView.text=price!!.toBigDecimal().setScale(2, RoundingMode.UP).toDouble().toString()+  " $";
}
@BindingAdapter("productItemData")
fun productItemDataRecyclerView(recyclerView: RecyclerView, data: List<Product>?) {
    val adapter = recyclerView.adapter as ProductAdapter
    adapter.submitList(data)
}
@BindingAdapter("product1688ItemData")
fun product1688ItemDataRecyclerView(recyclerView: RecyclerView, data: List<SkuItem1688Rvc>?) {
    val adapter = recyclerView.adapter as Product1688SkuItemAdapter
    adapter.submitList(data)
}
@BindingAdapter("productSkuItemData")
fun productSkuItemDataRecyclerView(recyclerView: RecyclerView, data: List<ProductSkuItemViewModel>?) {
    val adapter = recyclerView.adapter as ProductSkuItemAdapter
    adapter.submitList(data)
}
@BindingAdapter("Score")
fun bindScore(textView: TextView, score: Double?) {
    textView.text= App.resourses!!.getString(R.string.score)+score.toString();
}
@BindingAdapter("bindInt")
fun bindInt(textView: TextView, value: Int?) {
    if(value==null){
        textView.text=""
        return
    }
    textView.text=value.toString();
}
@BindingAdapter("cartItemData")
fun cartItemDataRecyclerView(recyclerView: RecyclerView, data: List<Cart>?) {
    val adapter = recyclerView.adapter as CartAdapter
    adapter.submitList(data)
}
@BindingAdapter("cartText")
fun bindCartText(textView: TextView, cart: Cart) {
    var text:String=App.resourses!!.getString(R.string.shop)+": " +cart.shopName
    text+=System.getProperty("line.separator")
    text+=cart.title
    if(!cart.skuText.isNullOrEmpty()){
        text+=System.getProperty("line.separator")
        text+=cart.skuText
    }
    text+=System.getProperty("line.separator")
    text+=App.resourses!!.getString(R.string.quantity)+": "+cart.quantity.toString()+" "+ App.resourses!!.getString(R.string.unit_price)+": "+cart.price.toString()+" $"
    textView.text=text;
}
@BindingAdapter("orderItemData")
fun orderItemDataRecyclerView(recyclerView: RecyclerView, data: List<Cart>?) {
    val adapter = recyclerView.adapter as OrderItemAdapter
    adapter.submitList(data)
}
@BindingAdapter("orderDetailItemData")
fun orderDetailItemDataRecyclerView(recyclerView: RecyclerView, data: List<Cart>?) {
    val adapter = recyclerView.adapter as OrderItemDetailAdapter
    adapter.submitList(data)
}
@BindingAdapter("Address")
fun bindAddressText(textView: TextView, address: String?) {
    if(address==null){
        textView.text=""
        return;
    }
    textView.text=address.split('(')[0];
}
@BindingAdapter("depositItemData")
fun depositItemDataRecyclerView(recyclerView: RecyclerView, data: List<Deposit>?) {
    val adapter = recyclerView.adapter as DepositAdapter
    adapter.submitList(data)
}
@BindingAdapter("contactItemData")
fun contactItemDataRecyclerView(recyclerView: RecyclerView, data: List<Contact>?) {
    val adapter = recyclerView.adapter as ContactAdapter
    adapter.submitList(data)
}
@BindingAdapter("boxItemData")
fun boxItemDataRecyclerView(recyclerView: RecyclerView, data: List<Box>?) {
    val adapter = recyclerView.adapter as BoxAdapter
    adapter.submitList(data)
}
@BindingAdapter("accountLinkItemData")
fun accountLinkItemDataRecyclerView(recyclerView: RecyclerView, data: List<User>?) {
    val adapter = recyclerView.adapter as AccountLinkAdapter
    adapter.submitList(data)
}
@BindingAdapter("strikeThrough")
fun strikeThroughText(textView: TextView, text: String?) {
    if(text.isNullOrEmpty()){
        return
    }
    textView.text=text
    textView.apply {
        paintFlags=Paint.STRIKE_THRU_TEXT_FLAG
    }
}