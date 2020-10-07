package com.skailab.nikabuy.viewModels

import android.content.Context
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.skailab.nikabuy.R
import com.skailab.nikabuy.card.CardFragmentDirections
import com.skailab.nikabuy.models.Cart
import com.skailab.nikabuy.models.ProductCartParcelize
import com.skailab.nikabuy.models.ShopCart
import com.skailab.nikabuy.models.filter.OtFilter
import com.skailab.nikabuy.room.UserDao
import com.skailab.nikabuy.services.CartServiceApi
import com.skailab.nikabuy.services.OrderServiceApi
import com.skailab.nikabuy.services.ProductServiceApi
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import kotlinx.coroutines.launch


class CartViewModel( db: UserDao?) : BaseViewModel(db) {
    private val _hasCart = MutableLiveData<Boolean>()
    private val _selectAll = MutableLiveData<Boolean>()
    val selectAll: LiveData<Boolean> get() = _selectAll
    val hasCart: LiveData<Boolean> get() = _hasCart
    private val _total = MutableLiveData<Double>()
    val total: LiveData<Double> get()=_total

    private val _shopCarts = MutableLiveData<MutableList<ShopCart>>()
    val shopCarts: LiveData<MutableList<ShopCart>> get() = _shopCarts

    init {
        _shopCarts.value= mutableListOf()
        _hasCart.value=true
        _selectAll.value=false
        _total.value=0.0
    }
    fun getCarts(context:Context){
        uiScope.launch {
            try {
                showWaiting(context)
                val result = CartServiceApi.retrofitService.getCartsGroupByShopAsync(userEntity.value!!.buyerId).await()
                hideWaiting()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{
                    _shopCarts.value=result.shops
                    if(_shopCarts.value!!.count()==0){
                        _hasCart.value=false
                    }
                }
            } catch (e: Exception) {
                displayException(context,e)
            }
        }
    }
    fun getProductDetail(context: Context,itemId:String,container: ViewGroup){
        uiScope.launch {
            try {
                showWaiting(context)
                val filter= OtFilter()
                filter.buyerId=userEntity.value!!.buyerId
                filter.itemId=itemId
                val result = ProductServiceApi.retrofitService.getProductDetailAsync(filter).await()
                hideWaiting()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{

                    container.findNavController().navigate(
                        CardFragmentDirections.actionCardFragmentToProductDetailFragment(
                            result.product!!
                        ))
                }
            } catch (e: Exception) {
                displayException(context,e)
            }
        }
    }
    fun  delete(context: Context, cart:Cart,shopCart: ShopCart,adapter: SectionedRecyclerViewAdapter){
        uiScope.launch {
            try {
                showWaiting(context)
                val result = CartServiceApi.retrofitService.deleteAsync(userEntity.value!!.buyerId,cart.id!!).await()
                hideWaiting()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{
                    shopCart.carts!!.remove(cart)
                    adapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                displayException(context,e)
            }
        }
    }
    fun setSelectAll(selectAll:Boolean ,adapter: SectionedRecyclerViewAdapter){
        _selectAll.value=selectAll
        _shopCarts.value!!.forEach {
            it.isSelected=selectAll
            it.carts!!.forEach{
                it.isSelected=selectAll
            }
        }
        adapter.notifyDataSetChanged()
        calulateTotal()
    }
    fun calulateTotal(){
        var total:Double=0.0
        _shopCarts.value!!.forEach {
            it.carts!!.forEach { cart->
                run {
                    if (cart.isSelected!!) {
                        total += cart.price!! * cart.quantity!!
                    }
                }
            }
        }
        _total.value=getThreeDigit(total)

    }
    fun onSubmitCart(context: Context, container: ViewGroup){
        val sbmCarts = arrayListOf<Cart>()
        _shopCarts.value!!.forEach {
            it.carts!!.forEach {
                if(it.isSelected!!){
                    sbmCarts.add(it)
                }
            }
        }
        if(sbmCarts.count()==0){
            showMadal(context,context.getString(R.string.cart_to_submit_require))
            return
        }
        var prCarts= ProductCartParcelize(userEntity.value!!.buyerId,"",null,sbmCarts,getDefaultLanguage())
        uiScope.launch {
            try {
                showWaiting(context)
                val result = OrderServiceApi.retrofitService.getCreateOrderSubmitFromCartsAsync(prCarts).await()
                hideWaiting()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{
                    container.findNavController().navigate(CardFragmentDirections.actionCardFragmentToNewOrderFragment(result.order!!))
                }
            } catch (e: Exception) {
                displayException(context,e)
            }
        }
    }
}