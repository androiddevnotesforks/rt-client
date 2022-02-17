package com.automotivecodelab.featuredetailsbottomsheet.di

import android.app.DownloadManager
import android.content.Context
import com.apollographql.apollo3.ApolloClient
import com.automotivecodelab.featuredetailsbottomsheet.data.TorrentDetailsRemoteDataSource
import com.automotivecodelab.featuredetailsbottomsheet.data.TorrentDetailsRemoteDataSourceImpl
import com.automotivecodelab.featuredetailsbottomsheet.data.TorrentDetailsRepositoryImpl
import com.automotivecodelab.featuredetailsbottomsheet.data.TorrentDetailsServerApi
import com.automotivecodelab.featuredetailsbottomsheet.domain.TorrentDetailsRepository
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Scope

@DetailsFeatureScoped
@Component(modules = [DetailsModule::class], dependencies = [DetailsComponentDeps::class])
interface DetailsComponent {
    fun torrentDetailsRepository(): TorrentDetailsRepository
}

@Module(includes = [DetailsModule.Bindings::class])
internal class DetailsModule {

    @Module
    interface Bindings {
        @Binds
        fun bindTorrentDetailsRepository(
            impl: TorrentDetailsRepositoryImpl
        ): TorrentDetailsRepository

        @Binds
        fun bindTorrentDetailsRemoteDataSource(
            impl: TorrentDetailsRemoteDataSourceImpl
        ): TorrentDetailsRemoteDataSource
    }

    @DetailsFeatureScoped
    @Provides
    fun provideSystemDownloadManager(context: Context): DownloadManager {
        return context.applicationContext.getSystemService(
            Context.DOWNLOAD_SERVICE
        ) as DownloadManager
    }
}

interface DetailsComponentDeps {
    val torrentDetailsServerApi: TorrentDetailsServerApi
    val apolloClient: ApolloClient
    @Named(TorrentDetailsDiConstants.APP_NAME) fun appName(): String
    @Named(TorrentDetailsDiConstants.SERVER_URL) fun serverUrl(): String
    val context: Context
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class DetailsFeatureScoped
