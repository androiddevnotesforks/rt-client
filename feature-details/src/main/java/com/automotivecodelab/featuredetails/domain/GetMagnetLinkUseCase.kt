package com.automotivecodelab.featuredetails.domain

import javax.inject.Inject

class GetMagnetLinkUseCase @Inject constructor(
    private val repository: TorrentDetailsRepository
) {
    suspend operator fun invoke(torrentId: String): Result<String> {
        return repository.getMagnetLink(torrentId)
    }
}
