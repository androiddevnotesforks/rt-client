package com.automotivecodelab.featurerssfeeds.domain.models

import java.util.*

data class RssChannelEntry(
    val title: String,
    val link: String,
    val updated: Date,
    val author: String,
    val id: String,
    val isFavorite: Boolean
)
