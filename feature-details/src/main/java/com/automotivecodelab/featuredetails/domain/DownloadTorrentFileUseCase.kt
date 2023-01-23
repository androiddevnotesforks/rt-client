package com.automotivecodelab.featuredetails.domain

import javax.inject.Inject

class DownloadTorrentFileUseCase @Inject constructor(
    private val repository: TorrentDetailsRepository
) {
    operator fun invoke(torrentId: String, title: String): Result<Unit> {
        return repository.downloadTorrentFile(torrentId, title)
    }
}
