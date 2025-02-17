package com.automotivecodelab.featuredetails.domain

import android.net.Uri
import com.automotivecodelab.featuredetails.domain.models.TorrentDescription

interface TorrentDetailsRepository {
    suspend fun getTorrentDescription(torrentId: String, sduiVersion: Int):
        Result<TorrentDescription>
    suspend fun getMagnetLink(torrentId: String): Result<String>
    fun downloadTorrentFile(torrentId: String, title: String): Result<Unit>
    suspend fun downloadAndGetUriForTorrentFile(torrentId: String): Result<Uri>
}
