package com.skailab.nikabuy.viewModels

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.skailab.nikabuy.R
import com.skailab.nikabuy.models.Product1688
import com.skailab.nikabuy.models.Product1688Cart
import com.skailab.nikabuy.models.SkuItem1688
import com.skailab.nikabuy.models.SkuItem1688Rvc
import com.skailab.nikabuy.product1688.product1688AddCartFragmentDirections
import com.skailab.nikabuy.productTaobao.productTaobaoAddCartFragmentDirections
import com.skailab.nikabuy.room.UserDao
import com.skailab.nikabuy.services.CartServiceApi
import kotlinx.coroutines.launch


class Product1688AddCartViewModel( db: UserDao?) : BaseViewModel(db) {
    private val _product = MutableLiveData<Product1688>()
    val product: LiveData<Product1688> get() = _product
    private var _skuItems = MutableLiveData<MutableList<SkuItem1688Rvc>>()
    val skuItems: LiveData<MutableList<SkuItem1688Rvc>> get() = _skuItems
    var totalQty:Int=0
    init {
        _skuItems.value= mutableListOf()
        totalQty=0
    }
    fun setSkuItems(items:List<SkuItem1688>){
        _product.value!!.skuItems=items
        var t:MutableList<SkuItem1688Rvc> = mutableListOf()
        _skuItems.value!!.clear()
        _product.value!!.skuItems!!.forEach {
            if(it.quantity==null){
                it.quantity=0
            }
            t.add(SkuItem1688Rvc(it))
        }
        _skuItems.value=t
    }
    fun setProduct(product: Product1688){
        _product.value=product
        _product.value!!.quantity=1
        if(_product.value!!.mainSku==null && _product.value!!.skuItems==null){
            this.totalQty=_product.value!!.quantity!!
        }
    }
    fun  onAddCart(context: Context, container: ViewGroup,activtity: Activity){
        showWaiting(context)
        uiScope.launch {
            try {
                _product.value!!.buyerId=userEntity.value!!.buyerId
                val cart= Product1688Cart(getDefaultLanguage(),false,_product.value!!)
                val result = CartServiceApi.retrofitService.createAlibabaCartAsync(cart).await()
                hideWaiting()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{
                    MaterialAlertDialogBuilder(context)
                        .setTitle(context.resources.getString(R.string.app_name))
                        .setMessage(context.getString(R.string.cart_add_success))
                        .setNeutralButton(context.getString(R.string.go_to_cart)) { dialog, which ->
                            container.findNavController().navigate(R.id.cardFragment)
                        }.setPositiveButton(context.getString(R.string.no)) { dialog, which ->
                            activtity.onBackPressed()
                        }
                        .show()
                }

            } catch (e: Exception) {
                displayException(context,e)
            }
        }


    }
    fun  addPurchaseCart(context: Context, container: ViewGroup){
       showWaiting(context)
        uiScope.launch {
            try {
                _product.value!!.buyerId=userEntity.value!!.buyerId
                val cart= Product1688Cart(getDefaultLanguage(),true,_product.value!!)
                val result = CartServiceApi.retrofitService.createAlibabaCartAsync(cart).await()
                hideWaiting()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{
                    container.findNavController().navigate(product1688AddCartFragmentDirections.actionProduct1688AddCartFragmentToNewOrderFragment(result.order!!))
                }
            } catch (e: Exception) {
                displayException(context,e)
            }
        }
    }

}