package com.skailab.nikabuy.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.skailab.nikabuy.room.UserDao
import com.skailab.nikabuy.viewModels.Product1688AddCartViewModel
import com.skailab.nikabuy.viewModels.Product1688ViewModel


class Product1688AddCartViewModelFactory(
    private val db: UserDao
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(Product1688AddCartViewModel::class.java)) {
            return Product1688AddCartViewModel(db) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
