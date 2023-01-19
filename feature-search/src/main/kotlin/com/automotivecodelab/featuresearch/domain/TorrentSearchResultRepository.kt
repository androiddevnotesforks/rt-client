package com.automotivecodelab.featuresearch.domain

import androidx.paging.PagingData
import com.automotivecodelab.featuresearch.domain.models.Order
import com.automotivecodelab.featuresearch.domain.models.Sort
import com.automotivecodelab.featuresearch.domain.models.TorrentSearchResult
import kotlinx.coroutines.flow.Flow

interface TorrentSearchResultRepository {
    fun getSearchResultStream(
        query: String,
        sort: Sort,
        order: Order,
        feeds: List<String>?,
    ): Flow<PagingData<TorrentSearchResult>>
    suspend fun getSearchSuggestions(query: String): Result<List<String>>
    suspend fun getTrends(): Result<List<String>>
}
