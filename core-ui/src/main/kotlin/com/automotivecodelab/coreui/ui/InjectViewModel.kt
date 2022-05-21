package com.automotivecodelab.coreui.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel

// in case of passing a vm instance into arguments instead of lambda, function does not work
// properly, vm will be recreated in every recomposition
@Composable
inline fun <reified T : ViewModel> injectViewModel(
    crossinline vmInstanceCreator: () -> T
): T = viewModel(
    factory = object : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return vmInstanceCreator() as T
        }
    }
)
