package com.automotivecodelab.featuredetails.domain

import android.net.Uri
import javax.inject.Inject

class GetUriForTorrentFileUseCase @Inject constructor(
    private val repository: TorrentDetailsRepository
) {
    suspend operator fun invoke(torrentId: String): Result<Uri> {
        return repository.downloadAndGetUriForTorrentFile(torrentId)
    }
}