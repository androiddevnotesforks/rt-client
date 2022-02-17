package com.automotivecodelab.featurerssfeeds.domain

import com.automotivecodelab.featurerssfeeds.domain.models.RssChannel
import javax.inject.Inject

class GetRssChannelUseCase @Inject constructor(
    private val rssChannelRepository: RssChannelRepository
) {
    suspend operator fun invoke(threadId: String): Result<RssChannel> {
        return rssChannelRepository.getRssChannel(threadId)
    }
}
