package com.skailab.nikabuy.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.skailab.nikabuy.room.UserDao
import com.skailab.nikabuy.room.productcache.ProductDao
import com.skailab.nikabuy.viewModels.ProductDetailViewModel
import com.skailab.nikabuy.viewModels.ProductScoreViewModel

class ProdutScoreViewModelFactory(
    private val db: UserDao,
    private val productDb: ProductDao
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductScoreViewModel::class.java)) {
            return ProductScoreViewModel(db,productDb) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
