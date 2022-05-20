package com.automotivecodelab.featuredetails.domain

import com.automotivecodelab.featuredetails.domain.models.SDUIComponent
import com.automotivecodelab.featuredetails.domain.models.TorrentDescription
import javax.inject.Inject

class GetTorrentDescriptionUseCase @Inject constructor(
    private val repository: TorrentDetailsRepository
) {
    suspend operator fun invoke(torrentId: String): Result<TorrentDescription> {
        return repository.getTorrentDescription(torrentId, SDUIComponent.SDUI_VERSION)
    }
}
