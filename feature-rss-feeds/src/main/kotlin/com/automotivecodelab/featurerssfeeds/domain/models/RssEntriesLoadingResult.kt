package com.automotivecodelab.featurerssfeeds.domain.models

sealed class RssEntriesLoadingResult {
    class Success(val data: List<RssChannelEntry>) : RssEntriesLoadingResult()
    class Error(val t: Throwable?) : RssEntriesLoadingResult()
    object Loading : RssEntriesLoadingResult()
}