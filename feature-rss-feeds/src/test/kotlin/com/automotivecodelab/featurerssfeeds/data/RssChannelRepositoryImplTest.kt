package com.automotivecodelab.featurerssfeeds.data

import com.automotivecodelab.featurerssfeeds.domain.models.RssEntriesLoadingResult
import java.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import org.junit.Test

class RssChannelRepositoryImplTest {

    @Test
    fun get_rss_channel_for_already_existing_channel() {
        val repository = RssChannelRepositoryImpl(
            localDataSource = LocalDataSourceMock(false),
            remoteDataSource = RemoteDataSourceMock()
        )
        runBlocking {
            repository.observeRssChannel(
                RemoteDataSourceMock.RSS_CHANNEL_ID,
                emptyFlow()
            ).collect { result ->
                if (result is RssEntriesLoadingResult.Success) {
                    assert(result.data[0].id == RemoteDataSourceMock.ENTRY_ID)
                }
            }
        }
    }

    @Test
    fun get_rss_channel_for_not_existing_channel() {
        val repository = RssChannelRepositoryImpl(
            localDataSource = LocalDataSourceMock(true),
            remoteDataSource = RemoteDataSourceMock()
        )
        runBlocking {
            val id = UUID.randomUUID().toString()
            repository.observeRssChannel(id, emptyFlow()).collect { result ->
                if (result is RssEntriesLoadingResult.Success) {
                    assert(result.data[0].id == RemoteDataSourceMock.ENTRY_ID)
                }
            }
        }
    }
}

class LocalDataSourceMock(private val isRssChannelExists: Boolean) :
    RssChannelLocalDataSource {
    override suspend fun addRssChannel(rssChannel: RssChannelDatabaseModel) {
        TODO("Not yet implemented")
    }

    override suspend fun getRssChannelByThreadId(threadId: String): RssChannelDatabaseModel {
        return RssChannelDatabaseModel(
            threadId = threadId,
            title = "Title",
            isSubscribed = true
        )
    }

    override fun observeAll(): Flow<List<RssChannelDatabaseModel>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteRssChannel(rssChannel: RssChannelDatabaseModel) {
        TODO("Not yet implemented")
    }

    override suspend fun updateRssChannel(rssChannel: RssChannelDatabaseModel) {
        TODO("Not yet implemented")
    }

    override suspend fun isRssChannelExists(threadId: String): Boolean {
        return isRssChannelExists
    }
}

class RemoteDataSourceMock : RssChannelRemoteDataSource {
    companion object {
        const val RSS_CHANNEL_ID = "networkChannelId"
        const val ENTRY_ID = "entryId"
    }
    override suspend fun getRssChannel(threadId: String): RssChannelNetworkModel {
        return RssChannelNetworkModel(
            title = "Title",
            threadId = RSS_CHANNEL_ID,
            entries = listOf(
                RssChannelEntryNetworkModel(
                    title = "Entry title",
                    author = "Author",
                    id = ENTRY_ID,
                    link = "http://example.com",
                    updated = Date()
                )
            )
        )
    }

    override suspend fun subscribeToRssChannel(threadId: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun unsubscribeFromRssChannel(threadId: String): Result<Unit> {
        TODO("Not yet implemented")
    }
}
