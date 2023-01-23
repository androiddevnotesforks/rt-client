package com.automotivecodelab.featuredetails.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.automotivecodelab.featuredetails.domain.UserSettingsRepository
import com.automotivecodelab.featuredetails.domain.models.TorrentAction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserSettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
): UserSettingsRepository {
    private val TORRENT_DEFAULT_ACTION_KEY = stringPreferencesKey("action")
    override fun observeTorrentDefaultAction(): Flow<TorrentAction> {
        return dataStore.data.map { prefs ->
            val defaultActionName = prefs[TORRENT_DEFAULT_ACTION_KEY] ?: TorrentAction.OPEN.name
            TorrentAction.valueOf(defaultActionName)
        }
    }

    override suspend fun setDefaultAction(action: TorrentAction) {
        dataStore.edit { prefs ->
            prefs[TORRENT_DEFAULT_ACTION_KEY] = action.name
        }
    }
}