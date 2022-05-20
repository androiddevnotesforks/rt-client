package com.automotivecodelab.rtclient.di

import android.content.Context
import android.content.res.Resources
import com.automotivecodelab.featuredetails.di.TorrentDetailsDiConstants
import com.automotivecodelab.rtclient.R
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class AppModule {
    @Singleton
    @Provides
    fun provideResourcesManager(context: Context): Resources {
        return context.resources
    }

    @Provides
    @Named(TorrentDetailsDiConstants.APP_NAME)
    fun provideAppName(resources: Resources) = resources.getString(R.string.app_name)
}
