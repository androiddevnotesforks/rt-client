package com.automotivecodelab.featurerssfeeds.data

import java.util.*

data class RssChannelNetworkModel(
    val title: String,
    val threadId: String,
    val entries: List<RssChannelEntryNetworkModel>
)

data class RssChannelEntryNetworkModel(
    val title: String,
    val link: String,
    val updated: Date,
    val author: String,
    val id: String
)
