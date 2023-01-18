package com.automotivecodelab.featuresearch.ui

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.automotivecodelab.featurefavoritesapi.Favorite
import com.automotivecodelab.featurefavoritesapi.ObserveFavoritesUseCase
import com.automotivecodelab.featuresearch.domain.GetSearchSuggestionsUseCase
import com.automotivecodelab.featuresearch.domain.GetTrendsUseCase
import com.automotivecodelab.featuresearch.domain.SearchTorrentsUseCase
import com.automotivecodelab.featuresearch.domain.models.Order
import com.automotivecodelab.featuresearch.domain.models.Sort
import com.automotivecodelab.featuresearch.domain.models.TorrentSearchResult
import javax.inject.Inject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class SearchViewModel @Inject constructor(
    private val searchTorrentsUseCase: SearchTorrentsUseCase,
    private val getSearchSuggestionsUseCase: GetSearchSuggestionsUseCase,
    observeFavoritesUseCase: ObserveFavoritesUseCase,
    getTrendsUseCase: GetTrendsUseCase
) : ViewModel() {

    var searchResults by mutableStateOf<Flow<PagingData<TorrentSearchResult>>?>(null)
        private set

    var searchScreenState by mutableStateOf<SearchScreenState>(SearchScreenState.EmptyScreen)
        private set

    var searchBarState by mutableStateOf(SearchBarState.EMPTY)
        private set

    val favorites: StateFlow<List<Favorite>> = observeFavoritesUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var needClearFocus by mutableStateOf(false)
        private set

    private var trends: List<String>? = null

    init {
        viewModelScope.launch {
            getTrendsUseCase()
                .onSuccess {
                    if (searchBarState == SearchBarState.EMPTY) {
                        trends = it
                        searchScreenState = SearchScreenState.Trends(it)
                    }
                }
                .onFailure {
                    if (searchBarState == SearchBarState.EMPTY) {
                        searchScreenState = SearchScreenState.Hint
                    }
                }
        }
    }

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    var feedIdWithTitle by mutableStateOf<Pair<String, String>?>(null)
        private set

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

    fun onQueryChange(query: String) {
        feedIdWithTitle = null
        _query.value = query
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

    fun onFeedSelected(feedId: String, feedTitle: String) {
        if (searchBarState == SearchBarState.WITH_QUERY && feedIdWithTitle == null) {
            feedIdWithTitle = feedId to feedTitle
            search()
        }
    }

    fun onLoading() {
        searchScreenState = SearchScreenState.Loading
    }

    fun onResultsAvailable() {
        searchScreenState = SearchScreenState.Results
    }

    fun onNothingFound() {
        searchScreenState = SearchScreenState.NothingFound
    }

    fun onTrendClicked(trendValue: String) {
        searchBarState = SearchBarState.WITH_QUERY
        _query.value = trendValue
        search()
    }

    fun onSearchBarCloseIconClicked() {
        if (searchBarState == SearchBarState.EXPANDED) {
            needClearFocus = true
        }
        else {
            searchBarState = SearchBarState.EMPTY
            onQueryChange("")
        }
    }

    fun onSearchBarFocusChanged(isFocused: Boolean) {
        when  {
            searchBarState == SearchBarState.EXPANDED && !isFocused -> {
                if (searchScreenState is SearchScreenState.EmptyScreen) {
                    val _trends = trends
                    searchScreenState = if (_trends != null)
                        SearchScreenState.Trends(_trends)
                    else
                        SearchScreenState.Hint
                }
                searchBarState = if (query.value.isEmpty())
                    SearchBarState.EMPTY
                else
                    SearchBarState.WITH_QUERY
            }
            searchBarState != SearchBarState.EXPANDED && isFocused -> {
                if (searchScreenState !is SearchScreenState.Results)
                    searchScreenState = SearchScreenState.EmptyScreen
                searchBarState = SearchBarState.EXPANDED
            }
        }
    }

    fun onFocusCleared() {
        needClearFocus = false
    }

    fun clearFocus() {
        needClearFocus = true
    }

    fun onKeyboardSearchButtonClicked() {
        needClearFocus = true
        search()
    }

    fun onSuggestionClicked(suggestion: String) {
        needClearFocus = true
        onQueryChange(suggestion)
        search()
    }

    fun onScroll() {
        if (searchBarState == SearchBarState.EXPANDED) {
            needClearFocus = true
        }
    }

    private fun search() {
        if (_query.value.isNotEmpty()) {
            searchResults = searchTorrentsUseCase(
                query = _query.value,
                sort = sort,
                order = order,
                feed = feedIdWithTitle?.first
            )
                .cachedIn(viewModelScope)
        }
    }
}

sealed class SearchScreenState {
    object Loading : SearchScreenState()
    object NothingFound: SearchScreenState()
    object Results: SearchScreenState()
    class Trends(val trends: List<String>): SearchScreenState()
    object EmptyScreen: SearchScreenState()
    object Hint: SearchScreenState()
}

enum class SearchBarState {
    EMPTY, EXPANDED, WITH_QUERY
}
