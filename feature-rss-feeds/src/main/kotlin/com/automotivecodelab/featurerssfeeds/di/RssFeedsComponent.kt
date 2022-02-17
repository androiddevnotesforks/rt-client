package com.automotivecodelab.featurerssfeeds.di

import androidx.compose.material.ExperimentalMaterialApi
import com.apollographql.apollo3.ApolloClient
import com.automotivecodelab.common.FeatureScoped
import com.automotivecodelab.corenetwork.data.FirebaseRegistrationTokenHolder
import com.automotivecodelab.featuredetailsbottomsheet.di.DetailsComponent
import com.automotivecodelab.featurerssfeeds.data.*
import com.automotivecodelab.featurerssfeeds.domain.RssChannelRepository
import com.automotivecodelab.featurerssfeeds.ui.rssentriesscreen.RssEntriesViewModel
import com.automotivecodelab.featurerssfeeds.ui.rssfeedsscreen.RssFeedsViewModel
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory

@FeatureScoped
@Component(
    dependencies = [RssFeedsDeps::class, DetailsComponent::class],
    modules = [RssFeedsModule::class]
)
interface RssFeedsComponent {

    @AssistedFactory
    interface RssEntriesViewModelFactory {
        @ExperimentalMaterialApi
        fun create(
            @Assisted(RssFeedsDiConstants.THREAD_ID) threadId: String,
            @Assisted(RssFeedsDiConstants.TORRENT_ID) torrentId: String?
        ): RssEntriesViewModel
    }

    fun rssFeedsViewModel(): RssFeedsViewModel

    fun rssEntriesViewModelFactory(): RssEntriesViewModelFactory
}

@Module
interface RssFeedsModule {

    @Binds
    fun bindRssChannelLocalDataSourceImpl(
        impl: RssChannelLocalDataSourceImpl
    ): RssChannelLocalDataSource

    @Binds
    fun bindRssChannelRemoteDataSourceImpl(
        impl: RssChannelRemoteDataSourceImpl
    ): RssChannelRemoteDataSource

    @Binds
    fun bindRssChannelsRepository(impl: RssChannelRepositoryImpl): RssChannelRepository
}

interface RssFeedsDeps {
    val graphqlClient: ApolloClient
    val rssChannelDao: RssChannelDao
    val firebaseTokenHolder: FirebaseRegistrationTokenHolder
}
