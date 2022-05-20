package com.automotivecodelab.featuresearch.di

import com.apollographql.apollo3.ApolloClient
import com.automotivecodelab.common.FeatureScoped
import com.automotivecodelab.featuresearch.data.TorrentSearchRemoteDataSource
import com.automotivecodelab.featuresearch.data.TorrentSearchResultRepositoryImpl
import com.automotivecodelab.featuresearch.data.TorrentsRemoteDataSourceImpl
import com.automotivecodelab.featuresearch.domain.TorrentSearchResultRepository
import com.automotivecodelab.featuresearch.ui.SearchViewModel
import dagger.Binds
import dagger.Component
import dagger.Module

@FeatureScoped
@Component(
    dependencies = [SearchComponentDeps::class],
    modules = [SearchModule::class]
)
interface SearchComponent {
    fun searchViewModel(): SearchViewModel
}

@Module
interface SearchModule {
    @Binds
    fun bindTorrentSearchResultRepository(
        impl: TorrentSearchResultRepositoryImpl
    ): TorrentSearchResultRepository
    @Binds
    fun bindTorrentSearchResultRemoteDataSource(
        impl: TorrentsRemoteDataSourceImpl
    ): TorrentSearchRemoteDataSource
}

interface SearchComponentDeps {
    val graphQlClient: ApolloClient
}
