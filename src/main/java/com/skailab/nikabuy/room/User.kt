package com.skailab.nikabuy.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_nikabuy_table")
data class User(
    @PrimaryKey(autoGenerate = false)
    var id: Long = 0L,
    @ColumnInfo(name = "phone")
    var phone: String,
    @ColumnInfo(name = "buyerId")
    var buyerId: Int,
    @ColumnInfo(name = "buyerCode")
    var buyerCode: String,
    @ColumnInfo(name = "displayName")
    var displayName: String,
    @ColumnInfo(name = "userId")
    var userId: String,
    @ColumnInfo(name = "productProvider")
    var productProvider: String,
    @ColumnInfo(name = "audioSearchLanguage")
    var audioSearchLanguage: String
)
