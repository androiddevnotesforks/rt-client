package com.automotivecodelab.featurerssfeeds.domain

import com.automotivecodelab.featurefavoritesapi.ObserveFavoritesUseCase
import com.automotivecodelab.featurerssfeeds.domain.models.RssEntriesLoadingResult
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveRssEntriesUseCase @Inject constructor(
    private val rssChannelRepository: RssChannelRepository,
    private val observeFavoritesUseCase: ObserveFavoritesUseCase
) {
    operator fun invoke(threadId: String): Flow<RssEntriesLoadingResult> {
        return rssChannelRepository.observeRssChannel(threadId, observeFavoritesUseCase())
    }
}
