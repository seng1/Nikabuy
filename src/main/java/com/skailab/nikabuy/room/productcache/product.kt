package com.skailab.nikabuy.room.productcache

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product_nikabuy_table")
data class Product(
    @PrimaryKey(autoGenerate = true) var id: Long,
    @ColumnInfo(name = "itemId")
    var itemId:String="",
    @ColumnInfo(name = "title")
    var title: String,
    @ColumnInfo(name = "quantity")
    var quantity: Int,
    @ColumnInfo(name = "unitPrice")
    var unitPrice: Double,
    @ColumnInfo(name = "unitPriceInChn")
    var unitPriceInChn: Double,
    @ColumnInfo(name = "sourceUrl")
    var sourceUrl: String,
    @ColumnInfo(name = "imageUrl")
    var imageUrl: String,
    @ColumnInfo(name = "shopId")
    var shopId: String?,
    @ColumnInfo(name = "shopUrl")
    var shopUrl: String?,
    @ColumnInfo(name = "originalTitle")
    var originalTitle: String?,
    @ColumnInfo(name = "discountPriceRangetext")
    var discountPriceRangetext: String?,
    @ColumnInfo(name = "orginalPriceRangetext")
    var orginalPriceRangetext: String?,
    @ColumnInfo(name = "created_at") var createdAt: Long,
    @ColumnInfo(name = "quantitySoldText")
    var quantitySoldText: String?

)