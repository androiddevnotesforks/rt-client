package com.automotivecodelab.featurerssfeeds.ui.rssfeedsscreen

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.automotivecodelab.coreui.ui.Event
import com.automotivecodelab.featurerssfeeds.domain.*
import java.util.*
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RssFeedsViewModel @Inject constructor(
    private val addRssChannelUseCase: AddRssChannelWithUrlUseCase,
    observeRssChannelsUseCase: ObserveRssChannelsUseCase,
    private val deleteRssChannelUseCase: DeleteRssChannelUseCase,
    private val subscribeToRssFeedUseCase: SubscribeToRssChannelUseCase,
    private val unsubscribeFromRssChannelUseCase: UnsubscribeFromRssChannelUseCase
) : ViewModel() {

    val rssChannels = observeRssChannelsUseCase()
        .map { list ->
            list.map { rssChannel ->
                rssChannel.toUIModel()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    var newRssUrl by mutableStateOf("")
        private set

    var addRssChannelDialogState by mutableStateOf(AddRssChannelDialogState.HIDDEN)
        private set

    var error by mutableStateOf<Event<Throwable>?>(null)
        private set

    fun onRssUrlChange(value: String) {
        addRssChannelDialogState = AddRssChannelDialogState.SHOWN
        newRssUrl = value
    }

    fun onDialogStateChange(newState: AddRssChannelDialogState) {
        addRssChannelDialogState = newState
    }

    fun addRssChannel() {
        viewModelScope.launch {
            addRssChannelDialogState = AddRssChannelDialogState.LOADING
            addRssChannelUseCase(newRssUrl)
                .onFailure { t ->
                    if (t is InputMismatchException) {
                        addRssChannelDialogState = AddRssChannelDialogState.INPUT_ERROR
                    } else {
                        error = Event(t)
                        addRssChannelDialogState = AddRssChannelDialogState.HIDDEN
                    }
                }
                .onSuccess {
                    newRssUrl = ""
                    addRssChannelDialogState = AddRssChannelDialogState.HIDDEN
                }
        }
    }

    fun deleteRssChannel(rssChannel: RssChannelUIModel) {
        viewModelScope.launch {
            deleteRssChannelUseCase(rssChannel.toDomainModel())
        }
    }

    fun subscribeToRssChannel(rssChannel: RssChannelUIModel) {
        viewModelScope.launch {
            subscribeToRssFeedUseCase(rssChannel.toDomainModel())
        }
    }

    fun unsubscribeFromRssChannel(rssChannel: RssChannelUIModel) {
        viewModelScope.launch {
            unsubscribeFromRssChannelUseCase(rssChannel.toDomainModel())
        }
    }
}
