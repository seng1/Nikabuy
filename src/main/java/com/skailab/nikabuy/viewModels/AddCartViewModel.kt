package com.skailab.nikabuy.viewModels

import android.app.Activity
import android.content.Context
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.skailab.nikabuy.R
import com.skailab.nikabuy.card.CardFragmentDirections
import com.skailab.nikabuy.home.HomeFragmentDirections
import com.skailab.nikabuy.models.*
import com.skailab.nikabuy.productdetail.AddCartFragmentDirections
import com.skailab.nikabuy.room.UserDao
import com.skailab.nikabuy.services.CartServiceApi
import com.skailab.nikabuy.services.OrderServiceApi
import com.skailab.nikabuy.services.ProductServiceApi
import kotlinx.coroutines.launch

class AddCartViewModel( db: UserDao?) : BaseViewModel(db) {
    private val _product = MutableLiveData<ProductDetail>()
    val product: LiveData<ProductDetail> get() = _product
    private val _priceRange1 = MutableLiveData<PriceRange>()
    val priceRange1: LiveData<PriceRange> get() = _priceRange1
    private val _priceRange2 = MutableLiveData<PriceRange>()
    val priceRange2: LiveData<PriceRange> get() = _priceRange2
    private val _priceRange3 = MutableLiveData<PriceRange>()
    val priceRange3: LiveData<PriceRange> get() = _priceRange3
    private val _hasPriceRange = MutableLiveData<Boolean>()
    val hasPriceRange: LiveData<Boolean> get() = _hasPriceRange
    private val _total = MutableLiveData<Double>()
    val total: LiveData<Double> get() = _total
    private val _messageToCustomerService = MutableLiveData<String>()
    val messageToCustomerService: LiveData<String> get() = _messageToCustomerService
    private val _quantity = MutableLiveData<Int>()
    val quantity: LiveData<Int> get() = _quantity
    private val _price = MutableLiveData<Double>()
    val price: LiveData<Double> get() = _price
    private val _products = MutableLiveData<MutableList<ProductSkuItemViewModel>>()
    val products: LiveData<MutableList<ProductSkuItemViewModel>> get() = _products


    init {
        _product.value= ProductDetail()
        _priceRange1.value= PriceRange(null,null,null,"",false)
        _priceRange1.value!!.isHasPriceRange=false
        _priceRange2.value= PriceRange(null,null,null,"",false)
        _priceRange2.value!!.isHasPriceRange=false
        _priceRange3.value= PriceRange(null,null,null,"",false)
        _priceRange3.value!!.isHasPriceRange=false
        _hasPriceRange.value=false
        _total.value=0.0
        _messageToCustomerService.value=""
        _products.value= mutableListOf()
    }
    fun setProduct( product: ProductDetail){
        _product.value=product
        _price.value=product.unitPrice
        if(_product.value!=null && _product.value!!.priceRanges!=null && _product.value!!.priceRanges!!.count()>0){
            if(_product.value!!.priceRanges!!.count()>=1){
                _priceRange1.value=_product.value!!.priceRanges!![0]
            }
            if(_product.value!!.priceRanges!!.count()>=2){
                _priceRange2.value=_product.value!!.priceRanges!![1]
            }
            if(_product.value!!.priceRanges!!.count()>=3){
                _priceRange3.value=_product.value!!.priceRanges!![2]
            }
            _hasPriceRange.value=true
        }
        else{
            _hasPriceRange.value=false
        }
        if(_product.value!!.skuItems!=null){
            _product.value!!.skuItems!!.forEach {
                _products.value!!.add(ProductSkuItemViewModel(it))
            }
        }

    }
    fun calculateTotal(context: Context){
        if(_product.value!!.priceRanges!=null && _product.value!!.priceRanges!!.count()>0){
            var qty=0
            if(_product.value!!.isHaveSkus!!){
                _product.value!!.skuItems!!.forEach {
                    if(it.quantity!=null){
                        qty+=it.quantity!!
                    }
                }
            }
            else{
                if(_quantity.value!=null){
                    qty=_quantity.value!!
                }
            }
            val price=getPriceFromQuantityPriceRange(_product.value!!.priceRanges!!,qty,context)
            _product.value!!.unitPrice=price.price!!
            _product.value!!.unitPriceInChn=price.unitPriceInChn!!
            _price.value=price.price
            if(_product.value!!.skuItems!=null){
                _product.value!!.skuItems!!.forEach {
                    it.price=price.price!!
                    it.unitPriceInChn=price.unitPriceInChn!!
                }
                _products.value!!.forEach {
                    it.setPrice(price.price!!)
                }
            }
        }
        _total.value=0.0
        if(_product.value!!.isHaveSkus!!){
            var t =0.0
            _product.value!!.skuItems!!.forEach {
                if(it.quantity!=null) {
                    t+= (it.quantity!!*it.price!!)
                }
            }
            _total.value=getThreeDigit(t)
            return
        }
        if(_quantity.value!=null){
            _total.value=getThreeDigit( _quantity.value!!*_product.value!!.unitPrice)

        }

    }
    fun  setQuantity(qty:Int?,context: Context){
        _quantity.value=qty
        _product.value!!.quantity=qty
        calculateTotal(context)
    }
    fun  setMessageToCustomer(message:String){
       _messageToCustomerService.value=message
    }
    fun  onAddCart(context: Context,container: ViewGroup,activtity:Activity){
        showWaiting(context)
        uiScope.launch {
            try {
                val productCart=ProductCart(userEntity.value!!.buyerId,_messageToCustomerService.value!!,_product.value!!)
                val result = CartServiceApi.retrofitService.addAsync(productCart).await()
                hideWaiting()
                if(!result.isSucess){
                    showMadal(context,result.errorText)
                }
                else{
                    MaterialAlertDialogBuilder(context)
                        .setTitle(context.resources.getString(R.string.app_name))
                        .setMessage(context.getString(R.string.cart_add_success))
                        .setNeutralButton(context.getString(R.string.go_to_cart)) { dialog, which ->
                            container!!.findNavController().navigate(R.id.cardFragment)
                        }.setPositiveButton(context.getString(R.string.back)) { dialog, which ->
                            // Respond to positive button press
                            activtity.onBackPressed()
                        }
                        .show()
                }

            } catch (e: Exception) {
                displayException(context,e)
            }
        }

    }
    fun  onBuyNow(context: Context,container: ViewGroup){
        val productCart=ProductCartParcelize(userEntity.value!!.buyerId,_messageToCustomerService.value!!,_product.value!!)
        //container!!.findNavController().navigate(AddCartFragmentDirections.actionAddCartFragmentToNewOrderFragment(productCart))
    }
}