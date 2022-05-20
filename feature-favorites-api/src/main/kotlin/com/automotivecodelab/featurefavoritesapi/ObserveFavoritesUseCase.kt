package com.automotivecodelab.featurefavoritesapi

import kotlinx.coroutines.flow.Flow

fun interface ObserveFavoritesUseCase {
    operator fun invoke(): Flow<List<Favorite>>
}