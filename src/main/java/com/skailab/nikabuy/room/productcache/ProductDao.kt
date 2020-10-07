package com.skailab.nikabuy.room.productcache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(product: Product)
    @Query("SELECT * FROM product_nikabuy_table ORDER BY created_at DESC LIMIT :limit OFFSET :offset")
    fun loadAllProductsByPage(limit: Int, offset: Int): Array<Product?>?
}

