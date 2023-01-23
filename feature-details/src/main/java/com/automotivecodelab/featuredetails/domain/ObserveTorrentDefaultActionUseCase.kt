package com.automotivecodelab.featuredetails.domain

import com.automotivecodelab.featuredetails.domain.models.TorrentAction
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveTorrentDefaultActionUseCase @Inject constructor(
    private val repository: UserSettingsRepository
) {
    operator fun invoke(): Flow<TorrentAction> {
        return repository.observeTorrentDefaultAction()
    }
}