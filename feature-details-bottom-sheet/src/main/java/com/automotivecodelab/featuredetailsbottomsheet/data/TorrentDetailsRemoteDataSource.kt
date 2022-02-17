package com.automotivecodelab.featuredetailsbottomsheet.data

import android.app.DownloadManager
import android.net.Uri
import android.os.Environment
import com.apollographql.apollo3.ApolloClient
import com.automotivecodelab.featuredetailsbottomsheet.MagnetLinkQuery
import com.automotivecodelab.featuredetailsbottomsheet.di.TorrentDetailsDiConstants
import com.automotivecodelab.featuredetailsbottomsheet.domain.models.TorrentDescription
import javax.inject.Inject
import javax.inject.Named

internal interface TorrentDetailsRemoteDataSource {
    suspend fun getTorrentDescription(torrentId: String, sduiVersion: Int): TorrentDescription
    suspend fun getMagnetLink(torrentId: String): String
    fun downloadTorrentFile(torrentId: String, title: String)
}

internal class TorrentDetailsRemoteDataSourceImpl @Inject constructor(
    private val serverApi: TorrentDetailsServerApi,
    private val graphqlClient: ApolloClient,
    private val downloadManager: DownloadManager,
    @Named(TorrentDetailsDiConstants.APP_NAME) private val appName: String,
    @Named(TorrentDetailsDiConstants.SERVER_URL) private val serverUrl: String
) : TorrentDetailsRemoteDataSource {

    override suspend fun getTorrentDescription(
        torrentId: String,
        sduiVersion: Int
    ): TorrentDescription {
        return serverApi.getTorrentDescription(torrentId, sduiVersion)
    }

    override suspend fun getMagnetLink(torrentId: String): String {
        val response = graphqlClient.query(MagnetLinkQuery(torrentId)).execute()
        return response.dataAssertNoErrors.magnetLink
    }

    override fun downloadTorrentFile(torrentId: String, title: String) {
        val filename = "${title
            .replace("/", " ")
            .replace("   ", " ")
            .take(120)
        }.torrent"
        val request = DownloadManager.Request(
            Uri.parse("${serverUrl}torrent/file?id=$torrentId")
        )
            .apply {
                setTitle(filename)
                setDescription(appName)
                setNotificationVisibility(
                    DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
                )
                setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename)
            }
        downloadManager.enqueue(request)
    }
}
