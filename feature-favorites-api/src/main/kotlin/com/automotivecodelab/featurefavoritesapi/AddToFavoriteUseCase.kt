package com.automotivecodelab.featurefavoritesapi

fun interface AddToFavoriteUseCase {
    suspend operator fun invoke(favorite: Favorite)
}
