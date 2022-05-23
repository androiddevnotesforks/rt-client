package com.automotivecodelab.featurerssfeeds.domain

import com.automotivecodelab.featurefavoritesapi.Favorite
import com.automotivecodelab.featurerssfeeds.domain.models.RssChannel
import com.automotivecodelab.featurerssfeeds.domain.models.RssEntriesLoadingResult
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.flow.Flow

interface RssChannelRepository {
    fun observeAll(): Flow<List<RssChannel>>
    suspend fun addRssChannel(threadId: String): Result<Unit>
    suspend fun deleteRssChannel(rssChannel: RssChannel)
    fun observeRssChannel(threadId: String, favorites: Flow<List<Favorite>>)
    : Flow<RssEntriesLoadingResult>
    suspend fun subscribeToRssChannel(rssChannel: RssChannel)
    suspend fun unsubscribeFromRssChannel(rssChannel: RssChannel)
}
