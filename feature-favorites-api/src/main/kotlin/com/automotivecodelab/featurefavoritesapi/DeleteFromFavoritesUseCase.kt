package com.automotivecodelab.featurefavoritesapi

interface DeleteFromFavoritesUseCase {
    suspend operator fun invoke(favorite: Favorite)
}