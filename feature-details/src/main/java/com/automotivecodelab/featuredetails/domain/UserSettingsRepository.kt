package com.automotivecodelab.featuredetails.domain

import com.automotivecodelab.featuredetails.domain.models.TorrentAction
import kotlinx.coroutines.flow.Flow

interface UserSettingsRepository {
    fun observeTorrentDefaultAction(): Flow<TorrentAction>
    suspend fun setDefaultAction(action: TorrentAction)
}