package com.automotivecodelab.featuredetailsbottomsheet.domain

import javax.inject.Inject

internal class GetMagnetLinkUseCase @Inject constructor(
    private val repository: TorrentDetailsRepository
) {
    suspend operator fun invoke(torrentId: String): Result<String> {
        return repository.getMagnetLink(torrentId)
    }
}
