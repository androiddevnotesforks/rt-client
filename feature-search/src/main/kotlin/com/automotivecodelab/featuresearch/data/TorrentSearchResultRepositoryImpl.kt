package com.automotivecodelab.featuresearch.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.automotivecodelab.common.FeatureScoped
import com.automotivecodelab.featuresearch.domain.TorrentSearchResultRepository
import com.automotivecodelab.featuresearch.domain.models.Order
import com.automotivecodelab.featuresearch.domain.models.Sort
import com.automotivecodelab.featuresearch.domain.models.TorrentSearchResult
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import timber.log.Timber

const val NETWORK_PAGE_SIZE = 10

@FeatureScoped
class TorrentSearchResultRepositoryImpl @Inject constructor(
    private val remoteDataSource: TorrentSearchRemoteDataSource
) : TorrentSearchResultRepository {
    override fun getSearchResultStream(
        query: String,
        sort: Sort,
        order: Order
    ): Flow<PagingData<TorrentSearchResult>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
            ),
            pagingSourceFactory = {
                TorrentSearchResultPagingSource(remoteDataSource, query, sort, order)
            }
        ).flow
    }

    override suspend fun getSearchSuggestions(query: String): Result<List<String>> {
        return runCatching {
            remoteDataSource.getSearchSuggestions(query)
        }
    }

    override suspend fun getTrends(): Result<List<String>> {
        val repeatCount = 3
        repeat(repeatCount) { index ->
            runCatching { remoteDataSource.getTrends() }
                .onSuccess { return Result.success(it) }
                .onFailure { t ->
                    Timber.e(t)
                    if (index == repeatCount - 1)
                        return Result.failure(t)
                    else
                        delay(500)
                }
        }
        error("Unreachable")
    }
}
