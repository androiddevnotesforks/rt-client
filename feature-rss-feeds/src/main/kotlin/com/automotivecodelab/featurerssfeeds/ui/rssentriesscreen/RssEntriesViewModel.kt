package com.automotivecodelab.featurerssfeeds.ui.rssentriesscreen

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.automotivecodelab.coreui.ui.Event
import com.automotivecodelab.featurerssfeeds.di.RssFeedsDiConstants
import com.automotivecodelab.featurerssfeeds.domain.GetRssChannelUseCase
import com.automotivecodelab.featurerssfeeds.domain.models.RssChannelEntry
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import timber.log.Timber

@ExperimentalMaterialApi
class RssEntriesViewModel @AssistedInject constructor(
    @Assisted(RssFeedsDiConstants.THREAD_ID) threadId: String,
    @Assisted(RssFeedsDiConstants.TORRENT_ID) torrentId: String?,
    private val getRssChannelUseCase: GetRssChannelUseCase
) : ViewModel() {

    val entries = mutableListOf<RssChannelEntry>()

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<Event<Throwable>?>(null)
        private set

    var closeScreenEvent by mutableStateOf<Event<Unit>?>(null)
        private set

    var openDetailsEvent by mutableStateOf<Event<RssChannelEntry>?>(null)

    init {
        viewModelScope.launch {
            isLoading = true
            getRssChannelUseCase(threadId)
                .onFailure {
                    error = Event(it)
                    closeScreenEvent = Event(Unit)
                }
                .onSuccess {
                    entries.clear()
                    entries.addAll(it.entries!!)
                    if (torrentId != null) {
                        val entryToOpen = it.entries.find {
                            rssChannelEntry ->
                            rssChannelEntry.id == torrentId
                        }
                        if (entryToOpen != null) {
                            openDetailsEvent = Event(entryToOpen)
                        } else {
                            error = Event(IndexOutOfBoundsException())
                        }
                    }
                }
            isLoading = false
        }
    }
}
