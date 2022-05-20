package com.automotivecodelab.featurefavoritesimpl.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.automotivecodelab.featurefavoritesapi.DeleteFromFavoritesUseCase
import com.automotivecodelab.featurefavoritesapi.Favorite
import com.automotivecodelab.featurefavoritesapi.ObserveFavoritesUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class FavoritesViewModel @Inject constructor(
    private val observeFavoritesUseCase: ObserveFavoritesUseCase,
    private val deleteFromFavoritesUseCase: DeleteFromFavoritesUseCase
): ViewModel() {
    val favorites: StateFlow<List<Favorite>> = observeFavoritesUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun deleteFavorite(favorite: Favorite) {
        viewModelScope.launch {
            deleteFromFavoritesUseCase(favorite)
        }
    }
}