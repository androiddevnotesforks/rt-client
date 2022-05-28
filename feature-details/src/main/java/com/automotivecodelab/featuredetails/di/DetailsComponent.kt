package com.automotivecodelab.featuredetails.di

import android.app.DownloadManager
import android.content.Context
import com.apollographql.apollo3.ApolloClient
import com.automotivecodelab.common.FeatureScoped
import com.automotivecodelab.featuredetails.data.TorrentDetailsRemoteDataSource
import com.automotivecodelab.featuredetails.data.TorrentDetailsRemoteDataSourceImpl
import com.automotivecodelab.featuredetails.data.TorrentDetailsRepositoryImpl
import com.automotivecodelab.featuredetails.data.TorrentDetailsServerApi
import com.automotivecodelab.featuredetails.domain.TorrentDetailsRepository
import com.automotivecodelab.featuredetails.ui.DetailsViewModel
import com.automotivecodelab.featurefavoritesapi.AddToFavoriteUseCase
import com.automotivecodelab.featurefavoritesapi.DeleteFromFavoritesUseCase
import com.automotivecodelab.featurefavoritesapi.ObserveFavoritesUseCase
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import javax.inject.Named

@FeatureScoped
@Component(
    modules = [DetailsModule::class],
    dependencies = [DetailsComponentDeps::class]
)
interface DetailsComponent {
    @AssistedFactory
    interface DetailsViewModelFactory {
        fun create(
            @Assisted(TorrentDetailsDiConstants.TORRENT_ID) torrentId: String,
            @Assisted(TorrentDetailsDiConstants.CATEGORY) category: String?,
            @Assisted(TorrentDetailsDiConstants.AUTHOR) author: String,
            @Assisted(TorrentDetailsDiConstants.TITLE) title: String,
            @Assisted(TorrentDetailsDiConstants.URL) url: String,
        ): DetailsViewModel
    }

    fun detailsViewModelFactory(): DetailsViewModelFactory
}

@Module(includes = [DetailsModule.Bindings::class])
class DetailsModule {

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

    @FeatureScoped
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
    val observeFavoritesUseCase: ObserveFavoritesUseCase
    val addToFavoritesUseCase: AddToFavoriteUseCase
    val deleteFromFavoritesUseCase: DeleteFromFavoritesUseCase
}
