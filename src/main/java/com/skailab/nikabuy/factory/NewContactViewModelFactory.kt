package com.skailab.nikabuy.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.skailab.nikabuy.room.UserDao
import com.skailab.nikabuy.viewModels.NewContactViewModel

class NewContactViewModelFactory(
    private val db: UserDao
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewContactViewModel::class.java)) {
            return NewContactViewModel(db) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}