package com.automotivecodelab.featuresearch.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.automotivecodelab.featuresearch.domain.models.Order
import com.automotivecodelab.featuresearch.domain.models.Sort
import com.automotivecodelab.featuresearch.domain.models.TorrentSearchResult
import timber.log.Timber

const val STARTING_PAGE_INDEX = 0

class TorrentSearchResultPagingSource(
    private val torrentsRemoteDataSource: TorrentSearchRemoteDataSource,
    private val query: String,
    private val sort: Sort,
    private val order: Order
) : PagingSource<Int, TorrentSearchResult>() {
    override fun getRefreshKey(state: PagingState<Int, TorrentSearchResult>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TorrentSearchResult> {
        val pageNumber = params.key ?: STARTING_PAGE_INDEX
        val loadSize = params.loadSize
        val startIndex = loadSize * pageNumber
        val endIndex = startIndex + loadSize
        return try {
            val response = torrentsRemoteDataSource.search(query, sort, order, startIndex, endIndex)
            LoadResult.Page(
                data = response,
                prevKey = if (pageNumber == STARTING_PAGE_INDEX) null else pageNumber - 1,
                nextKey = if (response.isEmpty()) null
                else pageNumber + (loadSize / NETWORK_PAGE_SIZE)
            )
        } catch (e: Exception) {
            Timber.d(e)
            return LoadResult.Error(e)
        }
    }
}
