package com.automotivecodelab.featuredetailsbottomsheet.ui

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.automotivecodelab.coreui.ui.Event
import com.automotivecodelab.featuredetailsbottomsheet.domain.GetMagnetLinkUseCase
import com.automotivecodelab.featuredetailsbottomsheet.domain.GetTorrentDescriptionUseCase
import com.automotivecodelab.featuredetailsbottomsheet.domain.GetTorrentFileUseCase
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

abstract class BottomSheetDetailsViewModel : ViewModel() {

    // encapsulation suffers because of dagger
    @Inject internal lateinit var getTorrentDescriptionUseCase: GetTorrentDescriptionUseCase
    @Inject internal lateinit var getMagnetLinkUseCase: GetMagnetLinkUseCase
    @Inject internal lateinit var getTorrentFileUseCase: GetTorrentFileUseCase

    internal var isDetailsLoading by mutableStateOf(false)
        private set

    internal var openBottomSheetEvent by mutableStateOf<Event<Unit>?>(null)
        private set

    internal var torrentDescription by mutableStateOf<TorrentDescriptionUIModel?>(null)
        private set

    internal var magnetLinkEvent by mutableStateOf<Event<String>?>(null)
        private set

    internal var isMagnetLinkLoading by mutableStateOf(false)
        private set

    internal var requestFilesystemPermissionEvent by mutableStateOf<Event<Unit>?>(null)

    @ExperimentalMaterialApi
    internal var modalBottomSheetState = ModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )

    private var torrentDescriptionRequestJob: Job? = null

    abstract fun setError(t: Throwable)

    @ExperimentalMaterialApi
    fun openDetails(
        torrentId: String,
        category: String?,
        author: String,
        title: String,
        url: String
    ) {
        torrentDescription = null // clear previous result
        torrentDescriptionRequestJob?.cancel()
        torrentDescriptionRequestJob = viewModelScope.launch {
            isDetailsLoading = true
            openBottomSheetEvent = Event(Unit)
            getTorrentDescriptionUseCase(torrentId)
                .onFailure {
                    torrentDescription = TorrentDescriptionUIModel(
                        id = torrentId,
                        category = category,
                        author = author,
                        title = title,
                        url = url,
                        threadId = null,
                        SDUIData = null,
                        downloads = null,
                        formattedSize = null,
                        seeds = null,
                        leeches = null,
                        timeAfterUpload = null,
                        state = null,
                        isWrongSDUIVersion = false
                    )
                    if (it !is CancellationException) {
                        setError(it)
                    }
                }
                .onSuccess { result ->
                    torrentDescription = result.toUIModel(
                        id = torrentId,
                        category = category,
                        author = author,
                        title = title,
                        url = url,
                        isWrongSDUIVersion = result.SDUIData == null
                    )
                }
            isDetailsLoading = false
        }
    }

    internal fun getMagnetLink(torrentId: String) {
        viewModelScope.launch {
            isMagnetLinkLoading = true
            getMagnetLinkUseCase(torrentId)
                .onFailure { setError(it) }
                .onSuccess { magnetLinkEvent = Event(it) }
            isMagnetLinkLoading = false
        }
    }

    internal fun getTorrentFile(torrentId: String, title: String) {
        getTorrentFileUseCase(torrentId, title)
            .onFailure { t ->
                when (t) {
                    is SecurityException -> {
                        requestFilesystemPermissionEvent = Event(Unit)
                    }
                }
            }
    }
}
