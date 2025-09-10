package com.example.menuapp.viewmodels

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner

/**
 * A custom factory for creating the MenuViewModel.
 * This is required to explicitly pass the SavedStateHandle to the ViewModel's constructor.
 */
@Suppress("UNCHECKED_CAST")
class MenuViewModelFactory(
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(MenuViewModel::class.java)) {
            return MenuViewModel(handle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
