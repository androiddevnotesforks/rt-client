package com.automotivecodelab.featurefavoritesimpl.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.automotivecodelab.featurefavoritesapi.DeleteFromFavoritesUseCase
import com.automotivecodelab.featurefavoritesapi.ObserveFavoritesUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class FavoritesViewModel @Inject constructor(
    observeFavoritesUseCase: ObserveFavoritesUseCase,
    private val deleteFromFavoritesUseCase: DeleteFromFavoritesUseCase
): ViewModel() {
    val favorites: StateFlow<List<FavoriteUIModel>> = observeFavoritesUseCase()
        .map { list -> list.map { favorite -> favorite.toUiModel() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteFavorite(favorite: FavoriteUIModel) {
        viewModelScope.launch {
            deleteFromFavoritesUseCase(favorite.toDomainModel())
        }
    }
}