package com.automotivecodelab.featurerssfeeds.data

import com.automotivecodelab.featurerssfeeds.domain.models.RssChannelEntry
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.*

class RssChannelRepositoryImplTest {

    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {
    }

    @Test
    fun get_rss_channel_for_already_existing_channel() {
        val repository = RssChannelRepositoryImpl(
            localDataSource = LocalDataSourceMock(false),
            remoteDataSource = RemoteDataSourceMock()
        )
        runBlocking {
            val result = repository.getRssChannel("id").getOrThrow()
            assert(result.threadId == RemoteDataSourceMock.RSS_CHANNEL_ID)
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
            val result = repository.getRssChannel(id).getOrThrow()
            assert(result.threadId == id)
        }
    }
}

class LocalDataSourceMock(private val isRssChannelExists: Boolean)
    : RssChannelLocalDataSource {
    override suspend fun addRssChannel(rssChannel: RssChannelDatabaseModel) {

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

class RemoteDataSourceMock: RssChannelRemoteDataSource {
    companion object {
        const val RSS_CHANNEL_ID = "networkChannelId"
    }
    override suspend fun getRssChannel(threadId: String): RssChannelNetworkModel {
        return RssChannelNetworkModel(
            title = "Title",
            threadId = RSS_CHANNEL_ID,
            entries = listOf(
                RssChannelEntry(
                    title = "Entry title",
                    author = "Author",
                    id = "id",
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