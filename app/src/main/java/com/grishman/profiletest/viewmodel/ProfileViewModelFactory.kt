package com.grishman.profiletest.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.grishman.profiletest.network.OpenpayService

/**
 * shiit
 */
class ProfileViewModelFactory(
        private val api: OpenpayService
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = ProfileViewModel(api) as T
}