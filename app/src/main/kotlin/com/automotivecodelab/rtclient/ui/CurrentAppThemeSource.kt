package com.automotivecodelab.rtclient.ui

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class AppTheme {
    LIGHT, DARK, AUTO
}

class CurrentAppThemeSource(private val context: Context) {
    companion object {
        private val Context.dataStore by preferencesDataStore(name = "settings")
        private val THEME_KEY = stringPreferencesKey("theme")
    }

    val appTheme: Flow<AppTheme> = context.dataStore.data
        .map { prefs ->
            val themeName = prefs[THEME_KEY] ?: AppTheme.AUTO.name
            try {
                AppTheme.valueOf(themeName)
            } catch (e: Exception) {
                AppTheme.AUTO
            }
        }

    suspend fun saveTheme(theme: AppTheme) {
        context.dataStore.edit { prefs ->
            prefs[THEME_KEY] = theme.name
        }
    }
}
