package com.skailab.nikabuy.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.skailab.nikabuy.room.UserDao
import com.skailab.nikabuy.viewModels.ProductDescriptionViewModel
import com.skailab.nikabuy.viewModels.ProductDetailViewModel

class ProductDescriptionViewModelFactory(
    private val db: UserDao
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductDescriptionViewModel::class.java)) {
            return ProductDescriptionViewModel(db) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
