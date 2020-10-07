package com.skailab.nikabuy.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.skailab.nikabuy.room.UserDao
import com.skailab.nikabuy.viewModels.AddCartViewModel
import com.skailab.nikabuy.viewModels.BottomImageViewModel

class BottomImageViewModelFactory(
    private val db: UserDao
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BottomImageViewModel::class.java)) {
            return BottomImageViewModel(db) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
