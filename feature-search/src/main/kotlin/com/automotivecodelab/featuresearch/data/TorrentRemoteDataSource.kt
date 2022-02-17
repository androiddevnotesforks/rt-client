package com.automotivecodelab.featuresearch.data

import com.apollographql.apollo3.ApolloClient
import com.automotivecodelab.featuresearch.SearchQuery
import com.automotivecodelab.featuresearch.SearchSuggestionsQuery
import com.automotivecodelab.featuresearch.domain.models.Order
import com.automotivecodelab.featuresearch.domain.models.Sort
import com.automotivecodelab.featuresearch.domain.models.TorrentSearchResult
import java.util.*
import javax.inject.Inject

interface TorrentSearchRemoteDataSource {
    suspend fun search(
        query: String,
        sort: Sort,
        order: Order,
        startIndex: Int,
        endIndex: Int
    ): List<TorrentSearchResult>
    suspend fun getSearchSuggestions(query: String): List<String>
}

class TorrentsRemoteDataSourceImpl @Inject constructor(
    private val graphqlClient: ApolloClient
) : TorrentSearchRemoteDataSource {

    override suspend fun search(
        query: String,
        sort: Sort,
        order: Order,
        startIndex: Int,
        endIndex: Int
    ): List<TorrentSearchResult> {
        val response = graphqlClient.query(
            SearchQuery(query, sort.value, order.value, startIndex, endIndex)
        ).execute()
        return response.dataAssertNoErrors.search.torrents.map {
            TorrentSearchResult(
                id = it.id,
                title = it.title,
                author = it.author,
                category = it.category,
                size = it.size.toLong(),
                downloads = it.downloads,
                formattedSize = it.formattedSize,
                url = it.url,
                state = it.state,
                registered = Date(it.registered.toLong()),
                seeds = it.seeds,
                leeches = it.leeches
            )
        }
    }

    override suspend fun getSearchSuggestions(query: String): List<String> {
        val response = graphqlClient.query(SearchSuggestionsQuery(query)).execute()
        return response.dataAssertNoErrors.getSearchSuggestions
    }
}
