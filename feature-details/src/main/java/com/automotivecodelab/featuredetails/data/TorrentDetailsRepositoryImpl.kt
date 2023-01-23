package com.automotivecodelab.featuredetails.data

import android.net.Uri
import com.automotivecodelab.common.FeatureScoped
import com.automotivecodelab.featuredetails.domain.TorrentDetailsRepository
import com.automotivecodelab.featuredetails.domain.models.TorrentDescription
import javax.inject.Inject

@FeatureScoped
class TorrentDetailsRepositoryImpl @Inject constructor(
    private val remoteDataSource: TorrentDetailsRemoteDataSource
) : TorrentDetailsRepository {
    override suspend fun getTorrentDescription(
        torrentId: String,
        sduiVersion: Int
    ): Result<TorrentDescription> {
        return runCatching { remoteDataSource.getTorrentDescription(torrentId, sduiVersion) }
    }

    override suspend fun getMagnetLink(torrentId: String): Result<String> {
        return runCatching { remoteDataSource.getMagnetLink(torrentId) }
    }

    override fun downloadTorrentFile(torrentId: String, title: String): Result<Unit> {
        return remoteDataSource.downloadTorrentFile(torrentId, title)
    }

    override suspend fun downloadAndGetUriForTorrentFile(torrentId: String): Result<Uri> {
        return runCatching { remoteDataSource.downloadAndGetUriForTorrentFile(torrentId) }
    }
}
