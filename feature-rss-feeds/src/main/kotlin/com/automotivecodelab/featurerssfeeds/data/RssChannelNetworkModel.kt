package com.automotivecodelab.featurerssfeeds.data

import com.automotivecodelab.featurerssfeeds.domain.models.RssChannel
import com.automotivecodelab.featurerssfeeds.domain.models.RssChannelEntry

data class RssChannelNetworkModel(
    val title: String,
    val threadId: String,
    val entries: List<RssChannelEntry>?
)

fun RssChannelNetworkModel.toDomainModel(isSubscribed: Boolean) = RssChannel(
    title = title,
    threadId = threadId,
    entries = entries,
    isSubscribed = isSubscribed
)
