package com.automotivecodelab.featurefavoritesimpl.domain

import com.automotivecodelab.featurefavoritesapi.AddToFavoriteUseCase
import com.automotivecodelab.featurefavoritesapi.Favorite
import javax.inject.Inject

class AddToFavoritesUseCaseImpl @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) : AddToFavoriteUseCase {
    override suspend fun invoke(favorite: Favorite) {
        favoritesRepository.addToFavorites(favorite)
    }
}