package com.automotivecodelab.featurerssfeeds.data

import com.automotivecodelab.featurefavoritesapi.Favorite
import com.automotivecodelab.featurerssfeeds.domain.models.RssChannel
import com.automotivecodelab.featurerssfeeds.domain.models.RssChannelEntry
import java.util.*

data class RssChannelNetworkModel(
    val title: String,
    val threadId: String,
    val entries: List<RssChannelEntryNetworkModel>
)

data class RssChannelEntryNetworkModel (
    val title: String,
    val link: String,
    val updated: Date,
    val author: String,
    val id: String
)
