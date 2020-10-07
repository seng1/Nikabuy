package com.skailab.nikabuy.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.skailab.nikabuy.room.UserDao
import com.skailab.nikabuy.room.productcache.ProductDao
import com.skailab.nikabuy.viewModels.TextViewModel

class TextViewModelFactory(
    private val db: UserDao,
    private val productDb: ProductDao
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TextViewModel::class.java)) {
            return TextViewModel(db,productDb) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
