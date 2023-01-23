package com.automotivecodelab.featuredetails.data

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.apollographql.apollo3.ApolloClient
import com.automotivecodelab.featuredetails.MagnetLinkQuery
import com.automotivecodelab.featuredetails.di.TorrentDetailsDiConstants
import com.automotivecodelab.featuredetails.domain.models.TorrentDescription
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import javax.inject.Inject
import javax.inject.Named


interface TorrentDetailsRemoteDataSource {
    suspend fun getTorrentDescription(torrentId: String, sduiVersion: Int): TorrentDescription
    suspend fun getMagnetLink(torrentId: String): String
    fun downloadTorrentFile(torrentId: String, title: String): Result<Unit>
    suspend fun downloadAndGetUriForTorrentFile(torrentId: String): Uri
}

class TorrentDetailsRemoteDataSourceImpl @Inject constructor(
    private val serverApi: TorrentDetailsServerApi,
    private val graphqlClient: ApolloClient,
    private val downloadManager: DownloadManager,
    private val context: Context,
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

    override suspend fun downloadAndGetUriForTorrentFile(torrentId: String): Uri {
        val file = File(context.filesDir, "temp.torrent")
        withContext(Dispatchers.IO) {
            if (file.exists()) file.delete()
            val url = URL("${serverUrl}torrent/file?id=$torrentId")
            url.openStream().use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
        }
        // authority string must be the same as declared in manifest
        return FileProvider
            .getUriForFile(context, "com.automotivecodelab.rtclient.fileprovider", file)
    }
}
