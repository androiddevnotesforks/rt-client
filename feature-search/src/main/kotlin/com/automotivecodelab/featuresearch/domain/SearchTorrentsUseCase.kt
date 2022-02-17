package com.automotivecodelab.featuresearch.domain

import androidx.paging.PagingData
import com.automotivecodelab.featuresearch.domain.models.Order
import com.automotivecodelab.featuresearch.domain.models.Sort
import com.automotivecodelab.featuresearch.domain.models.TorrentSearchResult
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class SearchTorrentsUseCase @Inject constructor(
    private val repository: TorrentSearchResultRepository
) {
    operator fun invoke(
        query: String,
        sort: Sort,
        order: Order
    ): Flow<PagingData<TorrentSearchResult>> {
        return repository.getSearchResultStream(query, sort, order)
    }
}
