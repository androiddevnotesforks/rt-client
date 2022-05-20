package com.automotivecodelab.rtclient.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides

private val Context.dataStore by preferencesDataStore(name = "settings")

@Module
class DataStoreModule {
    @Provides
    fun provideDataStore(context: Context) = context.dataStore
}