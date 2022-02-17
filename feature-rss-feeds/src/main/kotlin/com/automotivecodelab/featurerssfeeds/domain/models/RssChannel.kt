package com.automotivecodelab.featurerssfeeds.domain.models

data class RssChannel(
    val title: String,
    val threadId: String,
    val entries: List<RssChannelEntry>?,
    val isSubscribed: Boolean
)
