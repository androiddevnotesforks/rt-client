package com.automotivecodelab.rtclient.ui

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

enum class AppTheme {
    LIGHT, DARK, AUTO
}

@Singleton
class AppThemeSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val THEME_KEY = stringPreferencesKey("theme")

    val appTheme: Flow<AppTheme> = dataStore.data
        .map { prefs ->
            val themeName = prefs[THEME_KEY] ?: AppTheme.AUTO.name
            try {
                AppTheme.valueOf(themeName)
            } catch (e: Exception) {
                AppTheme.AUTO
            }
        }

    suspend fun saveTheme(theme: AppTheme) {
        dataStore.edit { prefs ->
            prefs[THEME_KEY] = theme.name
        }
    }
}
