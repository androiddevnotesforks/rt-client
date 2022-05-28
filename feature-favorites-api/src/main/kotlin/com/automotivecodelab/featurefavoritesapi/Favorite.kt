package com.automotivecodelab.featurefavoritesapi

data class Favorite(
    val torrentId: String,
    val threadId: String,
    val category: String,
    val author: String,
    val title: String,
    val url: String
)
