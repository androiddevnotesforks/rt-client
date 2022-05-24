package com.automotivecodelab.featurerssfeeds.ui.rssentriesscreen

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.automotivecodelab.coreui.ui.Event
import com.automotivecodelab.featurerssfeeds.di.RssFeedsDiConstants
import com.automotivecodelab.featurerssfeeds.domain.ObserveRssEntriesUseCase
import com.automotivecodelab.featurerssfeeds.domain.models.RssChannelEntry
import com.automotivecodelab.featurerssfeeds.domain.models.RssEntriesLoadingResult
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.*
import timber.log.Timber

@ExperimentalMaterialApi
class RssEntriesViewModel @AssistedInject constructor(
    @Assisted(RssFeedsDiConstants.THREAD_ID) threadId: String,
    @Assisted(RssFeedsDiConstants.TORRENT_ID) torrentId: String?,
    observeRssEntriesUseCase: ObserveRssEntriesUseCase
) : ViewModel() {

    val entries: StateFlow<RssEntriesLoadingResult> = observeRssEntriesUseCase(threadId)
        .map { result ->
            if (torrentId != null &&
                result is RssEntriesLoadingResult.Success &&
                openDetailsEvent == null) {
                val rssChannelEntry = result.data.find { it.id == torrentId }
                if (rssChannelEntry != null) {
                    openDetailsEvent = Event(rssChannelEntry)
                }
            }
            result
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            RssEntriesLoadingResult.Loading
        )

    var openDetailsEvent by mutableStateOf<Event<RssChannelEntry>?>(null)
}
