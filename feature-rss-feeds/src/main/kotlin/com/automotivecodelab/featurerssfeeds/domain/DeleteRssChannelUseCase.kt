package com.automotivecodelab.featurerssfeeds.domain

import com.automotivecodelab.featurerssfeeds.domain.models.RssChannel
import javax.inject.Inject

class DeleteRssChannelUseCase @Inject constructor(
    private val rssChannelRepository: RssChannelRepository,
    private val unsubscribeFromRssChannelUseCase: UnsubscribeFromRssChannelUseCase
) {
    suspend operator fun invoke(rssChannel: RssChannel) {
        rssChannelRepository.deleteRssChannel(rssChannel)
        unsubscribeFromRssChannelUseCase(rssChannel)
    }
}
