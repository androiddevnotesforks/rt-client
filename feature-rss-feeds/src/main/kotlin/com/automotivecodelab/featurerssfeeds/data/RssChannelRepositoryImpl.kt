package com.automotivecodelab.featurerssfeeds.data

import com.automotivecodelab.featurefavoritesapi.Favorite
import com.automotivecodelab.featurerssfeeds.domain.RssChannelRepository
import com.automotivecodelab.featurerssfeeds.domain.models.RssChannel
import com.automotivecodelab.featurerssfeeds.domain.models.RssChannelEntry
import com.automotivecodelab.featurerssfeeds.domain.models.RssEntriesLoadingResult
import javax.inject.Inject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class RssChannelRepositoryImpl @Inject constructor(
    private val remoteDataSource: RssChannelRemoteDataSource,
    private val localDataSource: RssChannelLocalDataSource
) : RssChannelRepository {

    override fun observeAll(): Flow<List<RssChannel>> {
        return localDataSource.observeAll().map {
            it.map { rssChannelDatabaseModel ->
                rssChannelDatabaseModel.toDomainModel(entries = emptyList())
            }
        }
    }

    override suspend fun addRssChannel(threadId: String): Result<Unit> {
        return runCatching {
            if (!localDataSource.isRssChannelExists(threadId)) {
                val networkRssChannel = remoteDataSource.getRssChannel(threadId)
                localDataSource.addRssChannel(
                    RssChannelDatabaseModel(
                        threadId = networkRssChannel.threadId,
                        title = networkRssChannel.title,
                        isSubscribed = false
                    )
                )
            }
        }
    }


    @OptIn(DelicateCoroutinesApi::class)
    override suspend fun deleteRssChannel(rssChannel: RssChannel) {
        GlobalScope.launch {
            localDataSource.deleteRssChannel(rssChannel.toDatabaseModel())
        }
    }

    override fun observeRssChannel(
        threadId: String,
        favorites: Flow<List<Favorite>>
    ): Flow<RssEntriesLoadingResult> {
        return flow {
            emit(RssEntriesLoadingResult.Loading)
            val networkRssChannelResult = runCatching { remoteDataSource.getRssChannel(threadId) }
            if (networkRssChannelResult.isSuccess) {
                val networkRssChannel = networkRssChannelResult.getOrThrow()
                if (!localDataSource.isRssChannelExists(threadId)) {
                    localDataSource.addRssChannel(
                        RssChannelDatabaseModel(
                            threadId = networkRssChannel.threadId,
                            title = networkRssChannel.title,
                            isSubscribed = false
                        )
                    )
                }
                favorites.collect { favoritesList ->
                    emit(
                        RssEntriesLoadingResult.Success(
                            networkRssChannel.entries.map { rssChannelEntryNetworkModel ->
                                RssChannelEntry(
                                    title = rssChannelEntryNetworkModel.title,
                                    link = rssChannelEntryNetworkModel.link,
                                    updated = rssChannelEntryNetworkModel.updated,
                                    author = rssChannelEntryNetworkModel.author,
                                    id = rssChannelEntryNetworkModel.id,
                                    isFavorite = favoritesList.any { favorite ->
                                        favorite.torrentId == rssChannelEntryNetworkModel.id
                                    }
                                )
                            }
                        )
                    )
                }
            } else {
                emit(RssEntriesLoadingResult.Error(networkRssChannelResult.exceptionOrNull()))
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override suspend fun subscribeToRssChannel(rssChannel: RssChannel) {
        GlobalScope.launch {
            localDataSource.updateRssChannel(
                rssChannel.copy(isSubscribed = true).toDatabaseModel()
            )
            remoteDataSource.subscribeToRssChannel(rssChannel.threadId)
                .onFailure {
                    localDataSource.updateRssChannel(
                        rssChannel.copy(isSubscribed = false).toDatabaseModel()
                    )
                }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override suspend fun unsubscribeFromRssChannel(rssChannel: RssChannel) {
        GlobalScope.launch {
            localDataSource.updateRssChannel(
                rssChannel.copy(isSubscribed = false).toDatabaseModel()
            )
            remoteDataSource.unsubscribeFromRssChannel(rssChannel.threadId)
                .onFailure {
                    localDataSource.updateRssChannel(
                        rssChannel.copy(isSubscribed = true).toDatabaseModel()
                    )
                }
        }
    }
}
