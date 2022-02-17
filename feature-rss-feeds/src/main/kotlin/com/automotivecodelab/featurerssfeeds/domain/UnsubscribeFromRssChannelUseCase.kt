package com.automotivecodelab.featurerssfeeds.domain

import com.automotivecodelab.featurerssfeeds.domain.models.RssChannel
import javax.inject.Inject

class UnsubscribeFromRssChannelUseCase @Inject constructor(
    private val rssChannelRepository: RssChannelRepository
) {
    suspend operator fun invoke(rssChannel: RssChannel) {
        rssChannelRepository.unsubscribeFromRssChannel(rssChannel)
    }
}
