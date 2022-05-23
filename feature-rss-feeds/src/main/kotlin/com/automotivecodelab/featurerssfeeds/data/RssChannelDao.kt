package com.automotivecodelab.featurerssfeeds.data

import androidx.room.*
import com.automotivecodelab.featurerssfeeds.domain.models.RssChannel
import com.automotivecodelab.featurerssfeeds.domain.models.RssChannelEntry
import kotlinx.coroutines.flow.Flow

interface RssChannelDatabase {
    fun rssChannelDao(): RssChannelDao
}

@Entity
data class RssChannelDatabaseModel(
    @PrimaryKey val threadId: String,
    val title: String,
    val isSubscribed: Boolean
)

@Dao
interface RssChannelDao {
    @Query("SELECT * FROM rsschanneldatabasemodel")
    suspend fun getAll(): List<RssChannelDatabaseModel>

    @Query("SELECT * FROM rsschanneldatabasemodel")
    fun observeAll(): Flow<List<RssChannelDatabaseModel>>

    @Query("SELECT * FROM rsschanneldatabasemodel WHERE threadId = :threadId")
    suspend fun getByThreadId(threadId: String): RssChannelDatabaseModel

    @Insert
    suspend fun insertAll(vararg rssChannelDatabaseModel: RssChannelDatabaseModel)

    @Delete
    suspend fun delete(rssChannelDatabaseModel: RssChannelDatabaseModel)

    @Update
    suspend fun update(rssChannelDatabaseModel: RssChannelDatabaseModel): Int

    @Query("SELECT EXISTS(SELECT * FROM rsschanneldatabasemodel WHERE threadId = :threadId)")
    suspend fun isFeedExists(threadId: String): Boolean
}

fun RssChannel.toDatabaseModel() = RssChannelDatabaseModel(
    title = title,
    threadId = threadId,
    isSubscribed = isSubscribed
)

fun RssChannelDatabaseModel.toDomainModel(entries: List<RssChannelEntry>) = RssChannel(
    title = title,
    threadId = threadId,
    entries = entries,
    isSubscribed = isSubscribed
)
