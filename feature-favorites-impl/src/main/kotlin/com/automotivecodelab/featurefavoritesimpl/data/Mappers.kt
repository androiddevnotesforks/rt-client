package com.automotivecodelab.featurefavoritesimpl.data

import com.automotivecodelab.featurefavoritesapi.Favorite

fun Favorite.toDbModel() = FavoriteDatabaseModel(
    torrentId = torrentId,
    threadId = threadId,
    category = category,
    author = author,
    title = title,
    url = url
)

fun FavoriteDatabaseModel.toDomainModel() = Favorite(
    torrentId = torrentId,
    threadId = threadId,
    category = category,
    author = author,
    title = title,
    url = url
)
