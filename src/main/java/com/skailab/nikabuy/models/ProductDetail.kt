package com.skailab.nikabuy.models

import android.os.Parcelable
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skailab.nikabuy.room.UserDao
import com.skailab.nikabuy.viewModels.BaseViewModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductDetail(
    var priceRanges:List<PriceRange>?=null,
    var title:String="",
    var quantity:Int?=null,
    var unitPrice:Double=0.0,
    var unitPriceInChn:Double=0.0,
    var subTotal:Double=0.0,
    var sourceUrl:String="",
    var imageUrl:String="",
    var imageUrls:List<String>?=null,
    var itemId:String="",
    var exchangeRate:Double=0.0,
    var shopId:String?="",
    var shopUrl:String?="",
    var quantitySoldText:String?="",
    var isLoading:Boolean?=false,
    var isVisible:Boolean?=false,
    var isAlreadyDisplay:Boolean?=false,
    var vendorInfo:Vendor?=null,
    var description:String?="",
    var categoryId:String?="",
    var brandId:String?="",
    var providerType:String?="",
    var skuItems:List<ProductSkuItem>?=null,
    var isHaveSkus:Boolean?=false,
    var originalTitle:String?="",
    var discountPriceRangetext:String?="",
    var orginalPriceRangetext:String?=""

) : Parcelable

@Parcelize
data class Product1688(
    var id:Int?,
    var title:String,
    var quantity:Int?=null,
    var providerType:String?,
    var unitPrice:Double?,
    var unitPriceInChn:Double?,
    var subTotal:Double?,
    var sourceUrl:String?,
    var imageUrl:String?,
    var imageUrls:List<String>?,
    var shopName:String?,
    var itemId:String?,
    var exchangeRate:Double?,
    var shopId:String?,
    var shopUrl:String?,
    var quantitySoldText:String?,
    var originalTitle:String?,
    var discountPriceRangetext:String?,
    var orginalPriceRangetext:String?,
    var categoryId:String?,
    var vendorInfo:Vendor?,
    var description:String?,
    var brandId:String?,
    var isHaveSkus:Boolean?,
    var buyerId:Int?,
    var priceRanges:List<PriceRange>?,
    var minQty:Int?,
    var masterQuantity:Int?,
    var skuItems:List<SkuItem1688>?,
    var mainSku:MainSku1688?,
    var totalQuantity:Int?,
    var total:Double?,
    var isAlreadyDisplay:Boolean?=false
    ):Parcelable
@Parcelize
data class ProductTaobao (
    var id:Int?,
    var title:String,
    var quantity:Int?=null,
    var providerType:String?,
    var unitPrice:Double?,
    var unitPriceInChn:Double?,
    var subTotal:Double?,
    var sourceUrl:String?,
    var imageUrl:String?,
    var imageUrls:List<String>?,
    var shopName:String?,
    var itemId:String?,
    var exchangeRate:Double?,
    var shopId:String?,
    var shopUrl:String?,
    var quantitySoldText:String?,
    var originalTitle:String?,
    var discountPriceRangetext:String?,
    var orginalPriceRangetext:String?,
    var categoryId:String?,
    var vendorInfo:Vendor?,
    var description:String?,
    var brandId:String?,
    var isHaveSkus:Boolean?,
    var buyerId:Int?,
    var skuMapModels:List<SkuMapModel>?,
    var skuModels:List<SkuModel>?,
    var minQty:Int?,
    var masterQuantity:Int?,
    var selectedSkuText:String?,
    var orginalSelectedSkuText:String?,
    var isAlreadyDisplay:Boolean?=false

    ) : Parcelable

@Parcelize
data class OtapiVendorInfo (
    var name:String?,
    var displayName:String?,
    var shopName:String?,
    var email:String?,
    var pictureUrl:String?,
    var feat:String?,
    var scores:Scores?,
    var credit:OtapiVendorRating?,
    var id:String?,
    var location:Location?,
    var goods:List<Product>?,
    var providerType:String?
    ) : Parcelable

@Parcelize
data class Scores (
    var deliveryScore:Double?,
    var itemScore:Double?,
    var serviceScore:Double?,
    var averageScore:Double?
    ) : Parcelable
@Parcelize
data class OtapiVendorRating (
    var level:Int?,
    var score:Int?,
    var totalFeedbacks:Int?,
    var positiveFeedbacks:Int?
    ) : Parcelable
@Parcelize
data class SkuItem1688 (
    var orignalSkuText:String?,
    var skuText:String?,
    var imageUrl:String?,
    var stockQty:Int?,
    var isHasImage:Boolean?,
    var quantity:Int?,
    var unitPriceInChn:Double?,
    var price:Double?,
    var miniImageUrl:String?
    ) : Parcelable
@Parcelize
data class MainSku1688 (
    var orignalPropertyTitle:String?,
    var propertyTitle:String?,
    var skus:List<Sku1688>?
) : Parcelable
@Parcelize
data class Sku1688 (
    var orignalSkuText:String?,
    var skuText:String?,
    var isHasImage:Boolean?,
    var miniImageUrl:String?,
    var imageUrl:String?,
    var skuItems:List<SkuItem1688>?,
    var isSelected:Boolean?= false
) : Parcelable
@Parcelize
data class SkuMapModel (
    var unitPriceInChn:Double?,
    var unitPrice:Double?,
    var originalMapKey:String?,
    var mapKey:String?,
    var stockQuantity:Int?
    ) : Parcelable
@Parcelize
data class SkuModel (
    var orignalTitle:String?,
    var title:String?,
    var skuItemModels:List<SkuItemModel>?,
    var selectedSkuItem:SkuItemModel?
    ) : Parcelable
@Parcelize
data class SkuItemModel (
    var orignalTitle:String?,
    var title:String?,
    var imageUrl:String?,
    var isHasImage:Boolean?,
    var miniImageUrl:String?,
    var isSelected:Boolean?=false,
    var skuText:String?
    //var skuModel:SkuModel?
    ) : Parcelable


data class TaobaoCart (
    var language:String?,
    var isAddToPurchase:Boolean?=false,
    var product:ProductTaobao
)
data class Product1688Cart (
    var language:String?,
    var isAddToPurchase:Boolean?=false,
    var product:Product1688
)
class SkuItem1688Rvc(var sku:SkuItem1688) {
    private val _quantity = MutableLiveData<Int>()
    val quantity: LiveData<Int> get() = _quantity
    var TextViewPrice: TextView? = null
    init {
        _quantity.value=sku.quantity
    }
}