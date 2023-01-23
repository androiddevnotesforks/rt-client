package com.automotivecodelab.featuredetails.ui

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.automotivecodelab.coreui.ui.Event
import com.automotivecodelab.featuredetails.di.TorrentDetailsDiConstants
import com.automotivecodelab.featuredetails.domain.*
import com.automotivecodelab.featuredetails.domain.models.TorrentAction
import com.automotivecodelab.featurefavoritesapi.AddToFavoriteUseCase
import com.automotivecodelab.featurefavoritesapi.DeleteFromFavoritesUseCase
import com.automotivecodelab.featurefavoritesapi.Favorite
import com.automotivecodelab.featurefavoritesapi.ObserveFavoritesUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DetailsViewModel @AssistedInject constructor(
    @Assisted(TorrentDetailsDiConstants.TORRENT_ID) private val torrentId: String,
    @Assisted(TorrentDetailsDiConstants.CATEGORY) private val category: String,
    @Assisted(TorrentDetailsDiConstants.AUTHOR) private val author: String,
    @Assisted(TorrentDetailsDiConstants.TITLE) private val title: String,
    @Assisted(TorrentDetailsDiConstants.URL) private val url: String,
    private val getTorrentDescriptionUseCase: GetTorrentDescriptionUseCase,
    private val getMagnetLinkUseCase: GetMagnetLinkUseCase,
    private val downloadTorrentFileUseCase: DownloadTorrentFileUseCase,
    observeFavoritesUseCase: ObserveFavoritesUseCase,
    private val addToFavoriteUseCase: AddToFavoriteUseCase,
    private val deleteFromFavoritesUseCase: DeleteFromFavoritesUseCase,
    private val getUriForTorrentFileUseCase: GetUriForTorrentFileUseCase,
    observeTorrentDefaultActionUseCase: ObserveTorrentDefaultActionUseCase,
    private val setTorrentDefaultActionUseCase: SetTorrentDefaultActionUseCase
) : ViewModel() {

    var isDetailsLoading by mutableStateOf(false)
        private set

    var torrentDescription by mutableStateOf<TorrentDescriptionUIModel?>(null)
        private set

    val isFavorite: StateFlow<Boolean> = observeFavoritesUseCase()
        .map { favorites ->
            favorites.any { favorite ->
                favorite.torrentId == torrentId
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    var magnetLinkEvent by mutableStateOf<Event<String>?>(null)
        private set

    var openTorrentFileEvent by mutableStateOf<Event<Uri>?>(null)
        private set

    var isMagnetLinkLoading by mutableStateOf(false)
        private set

    var isTorrentFileOpening by mutableStateOf(false)
        private set

    var requestFilesystemPermissionEvent by mutableStateOf<Event<Unit>?>(null)
        private set

    var error by mutableStateOf<Event<Throwable>?>(null)
        private set

    val defaultAction = observeTorrentDefaultActionUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TorrentAction.OPEN)

    private var toggleFavoriteJob: Job? = null

    init {
        viewModelScope.launch {
            isDetailsLoading = true
            getTorrentDescriptionUseCase(torrentId)
                .onFailure { t ->
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
                    error = Event(t)
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

    fun getMagnetLink(torrentId: String) {
        if (isMagnetLinkLoading) return
        viewModelScope.launch {
            isMagnetLinkLoading = true
            getMagnetLinkUseCase(torrentId)
                .onFailure { error = Event(it) }
                .onSuccess { magnetLinkEvent = Event(it) }
            isMagnetLinkLoading = false
        }
    }

    fun downloadTorrentFile(torrentId: String, title: String) {
        downloadTorrentFileUseCase(torrentId, title)
            .onFailure { t ->
                when (t) {
                    is SecurityException -> {
                        requestFilesystemPermissionEvent = Event(Unit)
                    }
                }
            }
    }

    fun openTorrentFile(torrentId: String) {
        if (isTorrentFileOpening) return
        viewModelScope.launch {
            isTorrentFileOpening = true
            getUriForTorrentFileUseCase(torrentId)
                .onFailure { error = Event(it) }
                .onSuccess { openTorrentFileEvent = Event(it) }
            isTorrentFileOpening = false
        }
    }

    fun toggleFavorite() {
        val torrentDescription = torrentDescription
        val threadId = torrentDescription?.threadId
        val category = torrentDescription?.category
        if (torrentDescription != null &&
            threadId != null &&
            category != null &&
            toggleFavoriteJob == null
        ) {
            toggleFavoriteJob = viewModelScope.launch {
                val favorite = Favorite(
                    torrentId = torrentDescription.id,
                    threadId = threadId,
                    category = category,
                    author = torrentDescription.author,
                    title = torrentDescription.title,
                    url = torrentDescription.url
                )
                if (isFavorite.value) deleteFromFavoritesUseCase(favorite)
                else addToFavoriteUseCase(favorite)
                toggleFavoriteJob = null
            }
        }
    }

    fun changeDefaultAction(action: TorrentAction) {
        viewModelScope.launch {
            setTorrentDefaultActionUseCase(action)
        }
    }
}
