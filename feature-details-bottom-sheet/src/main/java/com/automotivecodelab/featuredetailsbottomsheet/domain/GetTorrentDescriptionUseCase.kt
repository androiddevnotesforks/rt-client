package com.automotivecodelab.featuredetailsbottomsheet.domain

import com.automotivecodelab.featuredetailsbottomsheet.domain.models.SDUIComponent
import com.automotivecodelab.featuredetailsbottomsheet.domain.models.TorrentDescription
import javax.inject.Inject

internal class GetTorrentDescriptionUseCase @Inject constructor(
    private val repository: TorrentDetailsRepository
) {
    suspend operator fun invoke(torrentId: String): Result<TorrentDescription> {
        return repository.getTorrentDescription(torrentId, SDUIComponent.SDUI_VERSION)
    }
}
