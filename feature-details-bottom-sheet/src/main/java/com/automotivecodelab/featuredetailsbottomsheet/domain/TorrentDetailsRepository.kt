package com.automotivecodelab.featuredetailsbottomsheet.domain

import com.automotivecodelab.featuredetailsbottomsheet.domain.models.TorrentDescription

interface TorrentDetailsRepository {
    suspend fun getTorrentDescription(torrentId: String, sduiVersion: Int):
        Result<TorrentDescription>
    suspend fun getMagnetLink(torrentId: String): Result<String>
    fun downloadTorrentFile(torrentId: String, title: String)
}
