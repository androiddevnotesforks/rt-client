package com.automotivecodelab.featuredetails.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.automotivecodelab.coreui.ui.Event
import com.automotivecodelab.featuredetails.di.TorrentDetailsDiConstants
import com.automotivecodelab.featuredetails.domain.GetMagnetLinkUseCase
import com.automotivecodelab.featuredetails.domain.GetTorrentDescriptionUseCase
import com.automotivecodelab.featuredetails.domain.GetTorrentFileUseCase
import com.automotivecodelab.featurefavoritesapi.AddToFavoriteUseCase
import com.automotivecodelab.featurefavoritesapi.DeleteFromFavoritesUseCase
import com.automotivecodelab.featurefavoritesapi.Favorite
import com.automotivecodelab.featurefavoritesapi.ObserveFavoritesUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
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
    private val getTorrentFileUseCase: GetTorrentFileUseCase,
    observeFavoritesUseCase: ObserveFavoritesUseCase,
    private val addToFavoriteUseCase: AddToFavoriteUseCase,
    private val deleteFromFavoritesUseCase: DeleteFromFavoritesUseCase,
) : ViewModel() {

    var isDetailsLoading by mutableStateOf(false)
        private set

    var torrentDescription by mutableStateOf<TorrentDescriptionUIModel?>(null)
        private set

    var isFavorite: StateFlow<Boolean>? = null

    var magnetLinkEvent by mutableStateOf<Event<String>?>(null)
        private set

    var isMagnetLinkLoading by mutableStateOf(false)
        private set

    var requestFilesystemPermissionEvent by mutableStateOf<Event<Unit>?>(null)

    private var toggleFavoriteJob: Job? = null

    init {
        torrentDescription = null // clear previous result
        isFavorite = observeFavoritesUseCase()
            .map { favorites ->
                favorites.any { favorite ->
                    favorite.torrentId == torrentId
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)
        viewModelScope.launch {
            isDetailsLoading = true
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
                        TODO()
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

    fun getMagnetLink(torrentId: String) {
        viewModelScope.launch {
            isMagnetLinkLoading = true
            getMagnetLinkUseCase(torrentId)
                .onFailure { TODO() }
                .onSuccess { magnetLinkEvent = Event(it) }
            isMagnetLinkLoading = false
        }
    }

    fun getTorrentFile(torrentId: String, title: String) {
        getTorrentFileUseCase(torrentId, title)
            .onFailure { t ->
                when (t) {
                    is SecurityException -> {
                        requestFilesystemPermissionEvent = Event(Unit)
                    }
                }
            }
    }

    fun toggleFavorite() {
        val isFavorite = isFavorite?.value
        val torrentDescription = torrentDescription
        val threadId = torrentDescription?.threadId
        val category = torrentDescription?.category
        if (isFavorite != null &&
            torrentDescription != null &&
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
                if (isFavorite) deleteFromFavoritesUseCase(favorite)
                else addToFavoriteUseCase(favorite)
                toggleFavoriteJob = null
            }
        }
    }
}
