package com.automotivecodelab.featurefavoritesimpl.domain

import com.automotivecodelab.featurefavoritesapi.Favorite
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun observeFavorites(): Flow<List<Favorite>>
    suspend fun addToFavorites(favorite: Favorite)
    suspend fun removeFromFavorites(favorite: Favorite)
}