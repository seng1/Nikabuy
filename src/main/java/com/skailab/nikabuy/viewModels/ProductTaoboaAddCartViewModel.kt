package com.skailab.nikabuy.viewModels

import android.app.Activity
import android.content.Context
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.skailab.nikabuy.R
import com.skailab.nikabuy.models.ProductCart
import com.skailab.nikabuy.models.ProductTaobao
import com.skailab.nikabuy.models.TaobaoCart
import com.skailab.nikabuy.productTaobao.productTaobaoAddCartFragmentDirections
import com.skailab.nikabuy.room.UserDao
import com.skailab.nikabuy.services.CartServiceApi
import kotlinx.coroutines.launch

class ProductTaoboaAddCartViewModel( db: UserDao?) : BaseViewModel(db) {
    private val _product = MutableLiveData<ProductTaobao>()
    val product: LiveData<ProductTaobao> get() = _product
    init {

    }
    fun setProduct(product: ProductTaobao){
        _product.value=product
        _product.value!!.quantity=1
    }
    fun  onAddCart(context: Context, container: ViewGroup){
        showWaiting(context)
       uiScope.launch {
            try {
                _product.value!!.buyerId=userEntity.value!!.buyerId
                val cart= TaobaoCart(getDefaultLanguage(),false,_product.value!!)
                val result = CartServiceApi.retrofitService.createTaobaoCartAsync(cart).await()
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
                val cart= TaobaoCart(getDefaultLanguage(),true,_product.value!!)
                val result = CartServiceApi.retrofitService.createTaobaoCartAsync(cart).await()
                hideWaiting()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{
                    container!!.findNavController().navigate(productTaobaoAddCartFragmentDirections.actionProducttaobaoAddCartFragmentToNewOrderFragment(result.order!!))
                }
            } catch (e: Exception) {
                displayException(context,e)
            }
        }

    }



}