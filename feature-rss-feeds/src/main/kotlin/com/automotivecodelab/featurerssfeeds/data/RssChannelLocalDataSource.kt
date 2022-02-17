package com.automotivecodelab.featurerssfeeds.data

import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

interface RssChannelLocalDataSource {
    suspend fun addRssChannel(rssChannel: RssChannelDatabaseModel)
    suspend fun getRssChannelByThreadId(threadId: String): RssChannelDatabaseModel
    fun observeAll(): Flow<List<RssChannelDatabaseModel>>
    suspend fun deleteRssChannel(rssChannel: RssChannelDatabaseModel)
    suspend fun updateRssChannel(rssChannel: RssChannelDatabaseModel)
    suspend fun isRssChannelExists(threadId: String): Boolean
}

class RssChannelLocalDataSourceImpl @Inject constructor(
    private val rssChannelDao: RssChannelDao
) : RssChannelLocalDataSource {
    override suspend fun addRssChannel(rssChannel: RssChannelDatabaseModel) {
        withContext(Dispatchers.IO) {
            rssChannelDao.insertAll(rssChannel)
        }
    }

    override suspend fun getRssChannelByThreadId(threadId: String): RssChannelDatabaseModel {
        return withContext(Dispatchers.IO) {
            rssChannelDao.getByThreadId(threadId)
        }
    }

    override fun observeAll(): Flow<List<RssChannelDatabaseModel>> {
        return rssChannelDao.observeAll()
    }

    override suspend fun deleteRssChannel(rssChannel: RssChannelDatabaseModel) {
        withContext(Dispatchers.IO) {
            rssChannelDao.delete(rssChannel)
        }
    }

    override suspend fun updateRssChannel(rssChannel: RssChannelDatabaseModel) {
        withContext(Dispatchers.IO) {
            rssChannelDao.update(rssChannel)
        }
    }

    override suspend fun isRssChannelExists(threadId: String): Boolean {
        return withContext(Dispatchers.IO) {
            rssChannelDao.isFeedExists(threadId)
        }
    }
}
