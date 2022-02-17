package com.automotivecodelab.rtclient.di

import android.content.Context
import com.automotivecodelab.featuredetailsbottomsheet.di.DetailsComponentDeps
import com.automotivecodelab.featurerssfeeds.di.RssFeedsDeps
import com.automotivecodelab.featuresearch.di.SearchComponentDeps
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        NetworkModule::class,
        RoomModule::class
    ]
)
interface ApplicationComponent : DetailsComponentDeps, SearchComponentDeps, RssFeedsDeps {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): ApplicationComponent
    }
}
