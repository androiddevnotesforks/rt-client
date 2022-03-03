package com.automotivecodelab.featuredetailsbottomsheet.data

import com.automotivecodelab.featuredetailsbottomsheet.domain.TorrentDetailsRepository
import com.automotivecodelab.featuredetailsbottomsheet.domain.models.TorrentDescription
import javax.inject.Inject

internal class TorrentDetailsRepositoryImpl @Inject constructor(
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
}
