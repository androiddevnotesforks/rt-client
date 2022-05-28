package com.automotivecodelab.featuredetails.data

import android.app.DownloadManager
import android.net.Uri
import android.os.Environment
import com.apollographql.apollo3.ApolloClient
import com.automotivecodelab.featuredetails.MagnetLinkQuery
import com.automotivecodelab.featuredetails.di.TorrentDetailsDiConstants
import com.automotivecodelab.featuredetails.domain.models.TorrentDescription
import javax.inject.Inject
import javax.inject.Named

interface TorrentDetailsRemoteDataSource {
    suspend fun getTorrentDescription(torrentId: String, sduiVersion: Int): TorrentDescription
    suspend fun getMagnetLink(torrentId: String): String
    fun downloadTorrentFile(torrentId: String, title: String): Result<Unit>
}

class TorrentDetailsRemoteDataSourceImpl @Inject constructor(
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

    override fun downloadTorrentFile(torrentId: String, title: String): Result<Unit> {
        val forbiddenCharacters = """/\:*?"<>|"""
        var filename = title.take(120)
        forbiddenCharacters.forEach { char ->
            filename = filename.replace(char.toString(), " ")
        }
        while (filename.contains("  ")) {
            filename = filename.replace("  ", " ")
        }
        filename = "$filename.torrent"
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
        // on some devices (most of them runs with android 5, 6) download manager do not works
        // without WRITE_EXTERNAL_STORAGE permission
        return try {
            downloadManager.enqueue(request)
            Result.success(Unit)
        } catch (e: SecurityException) {
            Result.failure(e)
        }
    }
}
