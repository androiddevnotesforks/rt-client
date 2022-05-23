package com.automotivecodelab.featurerssfeeds.data

import com.apollographql.apollo3.ApolloClient
import com.automotivecodelab.corenetwork.data.FirebaseRegistrationTokenHolder
import com.automotivecodelab.featurerssfeeds.GetRssFeedQuery
import com.automotivecodelab.featurerssfeeds.SubscribeToRssMutation
import com.automotivecodelab.featurerssfeeds.UnsubscribeFromRssMutation
import com.automotivecodelab.featurerssfeeds.domain.models.RssChannelEntry
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

interface RssChannelRemoteDataSource {
    suspend fun getRssChannel(threadId: String): RssChannelNetworkModel
    suspend fun subscribeToRssChannel(threadId: String): Result<Unit>
    suspend fun unsubscribeFromRssChannel(threadId: String): Result<Unit>
}

class RssChannelRemoteDataSourceImpl @Inject constructor(
    private val graphQlClient: ApolloClient,
    private val firebaseRegistrationTokenHolder: FirebaseRegistrationTokenHolder
) : RssChannelRemoteDataSource {
    override suspend fun getRssChannel(threadId: String): RssChannelNetworkModel {
        val response = graphQlClient.query(GetRssFeedQuery(threadId)).execute()
        return response.dataAssertNoErrors.getRss.run {
            val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            df.timeZone = TimeZone.getTimeZone("UTC")
            RssChannelNetworkModel(
                title = title,
                threadId = threadId,
                entries = entries.map { entry ->
                    RssChannelEntryNetworkModel(
                        title = entry.title,
                        link = entry.link,
                        updated = df.parse(entry.updated)!!,
                        author = entry.author,
                        id = entry.id
                    )
                }
            )
        }
    }

    override suspend fun subscribeToRssChannel(threadId: String): Result<Unit> {
        return runCatching {
            val token = firebaseRegistrationTokenHolder.get()
            val response = graphQlClient.mutation(
                SubscribeToRssMutation(
                    threadId = threadId, token = token
                )
            ).execute()
            return if (response.hasErrors()) {
                Result.failure(IOException())
            } else {
                Result.success(Unit)
            }
        }
    }

    override suspend fun unsubscribeFromRssChannel(threadId: String): Result<Unit> {
        return runCatching {
            val token = firebaseRegistrationTokenHolder.get()
            val response = graphQlClient.mutation(
                UnsubscribeFromRssMutation(
                    threadId = threadId, token = token
                )
            ).execute()
            return if (response.hasErrors()) {
                Result.failure(IOException())
            } else {
                Result.success(Unit)
            }
        }
    }
}
