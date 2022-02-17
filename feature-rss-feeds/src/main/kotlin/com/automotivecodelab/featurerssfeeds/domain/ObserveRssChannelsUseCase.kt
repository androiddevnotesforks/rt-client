package com.automotivecodelab.featurerssfeeds.domain

import com.automotivecodelab.featurerssfeeds.domain.models.RssChannel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveRssChannelsUseCase @Inject constructor(
    private val rssChannelRepository: RssChannelRepository
) {
    operator fun invoke(): Flow<List<RssChannel>> {
        return rssChannelRepository.observeAll()
    }
}
