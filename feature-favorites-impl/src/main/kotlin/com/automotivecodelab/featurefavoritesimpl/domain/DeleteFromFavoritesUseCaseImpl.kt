package com.automotivecodelab.featurefavoritesimpl.domain

import com.automotivecodelab.featurefavoritesapi.DeleteFromFavoritesUseCase
import com.automotivecodelab.featurefavoritesapi.Favorite
import javax.inject.Inject

class DeleteFromFavoritesUseCaseImpl @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) : DeleteFromFavoritesUseCase {
    override suspend fun invoke(favorite: Favorite) {
        favoritesRepository.removeFromFavorites(favorite)
    }
}