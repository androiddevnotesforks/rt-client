package com.automotivecodelab.featurerssfeeds.ui.rssfeedsscreen

import com.automotivecodelab.featurerssfeeds.domain.models.RssChannel
import com.automotivecodelab.featurerssfeeds.domain.models.RssChannelEntry
import java.util.*

data class RssChannelUIModel(
    val title: String,
    val threadId: String,
    val entries: List<RssChannelEntry>,
    val isSubscribed: Boolean,
    // workaround for bug with removing a feed from list and then add again the same feed - feed
    // is dismissed after that:
    val uuid: UUID = UUID.randomUUID()
)

fun RssChannelUIModel.toDomainModel() = RssChannel(
    title = title,
    threadId = threadId,
    entries = entries,
    isSubscribed = isSubscribed
)

fun RssChannel.toUIModel() = RssChannelUIModel(
    title = title,
    threadId = threadId,
    entries = entries,
    isSubscribed = isSubscribed
)
