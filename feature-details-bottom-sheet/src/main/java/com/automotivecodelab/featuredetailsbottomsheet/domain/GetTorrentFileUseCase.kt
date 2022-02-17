package com.automotivecodelab.featuredetailsbottomsheet.domain

import javax.inject.Inject

internal class GetTorrentFileUseCase @Inject constructor(
    private val repository: TorrentDetailsRepository
) {
    operator fun invoke(torrentId: String, title: String) {
        repository.downloadTorrentFile(torrentId, title)
    }
}
