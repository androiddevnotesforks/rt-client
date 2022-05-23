package com.automotivecodelab.featuresearch.ui

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.automotivecodelab.coreui.ui.Event
import com.automotivecodelab.featuresearch.domain.GetSearchSuggestionsUseCase
import com.automotivecodelab.featuresearch.domain.SearchTorrentsUseCase
import com.automotivecodelab.featuresearch.domain.models.Order
import com.automotivecodelab.featuresearch.domain.models.Sort
import com.automotivecodelab.featuresearch.domain.models.TorrentSearchResult
import javax.inject.Inject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class SearchViewModel @Inject constructor(
    private val searchTorrentsUseCase: SearchTorrentsUseCase,
    private val getSearchSuggestionsUseCase: GetSearchSuggestionsUseCase
) : ViewModel() {

    var searchResults by mutableStateOf(emptyFlow<PagingData<TorrentSearchResult>>())
        private set

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    @ExperimentalCoroutinesApi
    @FlowPreview
    val searchSuggestions = _query
        .debounce(200)
        .mapLatest { query ->
            if (query.length < 2) {
                emptyList()
            } else {
                val result = getSearchSuggestionsUseCase(query)
                if (result.isSuccess) {
                    result.getOrThrow()
                } else {
                    emptyList()
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var sort by mutableStateOf(Sort.Registered)
        private set

    var order by mutableStateOf(Order.Desc)
        private set

    var error by mutableStateOf<Event<Throwable>?>(null)

    fun onQueryChange(query: String) {
        viewModelScope.launch {
            _query.value = query
        }
    }

    fun onSortChange(sort: Sort) {
        this.sort = sort
        search()
    }

    fun onOrderChange() {
        order = if (order == Order.Desc) {
            Order.Asc
        } else {
            Order.Desc
        }
        search()
    }

    fun search() {
        if (_query.value.isNotEmpty()) {
            searchResults = searchTorrentsUseCase(_query.value, sort, order)
        }
    }

    fun setError(t: Throwable) {
        error = Event(t)
    }
}
