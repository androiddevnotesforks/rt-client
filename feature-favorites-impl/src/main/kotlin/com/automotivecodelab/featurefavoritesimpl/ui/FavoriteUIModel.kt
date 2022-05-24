package com.automotivecodelab.featurefavoritesimpl.ui

import com.automotivecodelab.featurefavoritesapi.Favorite
import java.util.*

data class FavoriteUIModel(
    val torrentId: String,
    val threadId: String,
    val category: String,
    val author: String,
    val title: String,
    val url: String,
    val uuid: UUID = UUID.randomUUID()
)

fun FavoriteUIModel.toDomainModel() = Favorite(
    torrentId = torrentId,
    threadId = threadId,
    category = category,
    author = author,
    title = title,
    url = url
)

fun Favorite.toUiModel() = FavoriteUIModel(
    torrentId = torrentId,
    threadId = threadId,
    category = category,
    author = author,
    title = title,
    url = url
)
