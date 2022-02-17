package com.automotivecodelab.featurerssfeeds.data

import com.automotivecodelab.featurerssfeeds.domain.RssChannelRepository
import com.automotivecodelab.featurerssfeeds.domain.models.RssChannel
import javax.inject.Inject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RssChannelRepositoryImpl @Inject constructor(
    private val remoteDataSource: RssChannelRemoteDataSource,
    private val localDataSource: RssChannelLocalDataSource
) : RssChannelRepository {

    override fun observeAll(): Flow<List<RssChannel>> {
        return localDataSource.observeAll().map {
            it.map { rssChannelDatabaseModel ->
                rssChannelDatabaseModel.toDomainModel(entries = null)
            }
        }
    }

    @DelicateCoroutinesApi
    override suspend fun deleteRssChannel(rssChannel: RssChannel) {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                localDataSource.deleteRssChannel(rssChannel.toDatabaseModel())
            }
        }
    }

    override suspend fun getRssChannel(threadId: String): Result<RssChannel> {
        return runCatching {
            withContext(Dispatchers.IO) {
                val networkRssChannelDef = async { remoteDataSource.getRssChannel(threadId) }
                val isThreadAlreadyAddedLocallyDef = async {
                    localDataSource.isRssChannelExists(threadId)
                }
                val networkRssChannel = networkRssChannelDef.await()
                val isThreadAlreadyAddedLocally = isThreadAlreadyAddedLocallyDef.await()
                if (isThreadAlreadyAddedLocally) {
                    val localRssChannel = localDataSource
                        .getRssChannelByThreadId(threadId)
                    localRssChannel.toDomainModel(networkRssChannel.entries)
                } else {
                    val domainModel = networkRssChannel.toDomainModel(isSubscribed = false)
                    localDataSource.addRssChannel(domainModel.toDatabaseModel())
                    domainModel
                }
            }
        }
    }

    @DelicateCoroutinesApi
    override suspend fun subscribeToRssChannel(rssChannel: RssChannel): Result<Unit> {
        return runCatching {
            GlobalScope.launch {
                withContext(Dispatchers.IO) {
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
        }
    }

    @DelicateCoroutinesApi
    override suspend fun unsubscribeFromRssChannel(rssChannel: RssChannel): Result<Unit> {
        return runCatching {
            GlobalScope.launch {
                withContext(Dispatchers.IO) {
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
    }
}
