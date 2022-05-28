package com.automotivecodelab.rtclient.di

import android.content.Context
import com.automotivecodelab.featuredetails.di.DetailsComponentDeps
import com.automotivecodelab.featurefavoritesimpl.di.FavoritesComponentDeps
import com.automotivecodelab.featurefavoritesimpl.di.FavoritesModule
import com.automotivecodelab.featurerssfeeds.di.RssFeedsDeps
import com.automotivecodelab.featuresearch.di.SearchComponentDeps
import com.automotivecodelab.rtclient.ui.AppThemeSource
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        NetworkModule::class,
        RoomModule::class,
        DataStoreModule::class,
        FavoritesModule::class
    ]
)
interface ApplicationComponent :
    DetailsComponentDeps, SearchComponentDeps, RssFeedsDeps, FavoritesComponentDeps {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): ApplicationComponent
    }

    fun appThemeSource(): AppThemeSource
}
