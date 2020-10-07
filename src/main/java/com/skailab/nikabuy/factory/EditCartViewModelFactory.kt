package com.skailab.nikabuy.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.skailab.nikabuy.room.UserDao
import com.skailab.nikabuy.viewModels.EditCartViewModel
import com.skailab.nikabuy.viewModels.TextViewModel

class EditCartViewModelFactory(
    private val db: UserDao
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditCartViewModel::class.java)) {
            return EditCartViewModel(db) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
