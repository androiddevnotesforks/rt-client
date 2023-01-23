package com.automotivecodelab.featuredetails.domain

import com.automotivecodelab.featuredetails.domain.models.TorrentAction
import javax.inject.Inject

class SetTorrentDefaultActionUseCase @Inject constructor(
    private val repository: UserSettingsRepository
) {
    suspend operator fun invoke(action: TorrentAction) {
        repository.setDefaultAction(action)
    }
}