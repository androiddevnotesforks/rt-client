package com.automotivecodelab.featuresearch.ui

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.automotivecodelab.coreui.ui.theme.DefaultPadding
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

    private val _searchBarState = MutableStateFlow(SearchBarState.COLLAPSED)
    val searchBarState = _searchBarState.asStateFlow()

    val favorites: StateFlow<List<Favorite>> = observeFavoritesUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var needClearFocus by mutableStateOf(false)
        private set

    private var trends: List<String>? = null

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    var feedsIdWithTitle = mutableStateListOf<Pair<String, String>>()

    private var defaultSearchBarHeightValue = SearchBarHeight + DefaultPadding * 2

    var searchBarHeight by mutableStateOf(defaultSearchBarHeightValue)
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

    init {
        viewModelScope.launch {
            listOf(
                async {
                    getTrendsUseCase()
                        .onSuccess {
                            if (query.value.isEmpty()) {
                                trends = it
                                searchScreenState = SearchScreenState.Trends(it)
                            }
                        }
                        .onFailure {
                            if (query.value.isEmpty()) {
                                searchScreenState = SearchScreenState.Hint
                            }
                        }
                },
                async {
                    searchBarState.collect {
                        when (it) {
                            SearchBarState.COLLAPSED ->
                                searchBarHeight = defaultSearchBarHeightValue
                            SearchBarState.WITH_FEED ->
                                searchBarHeight = defaultSearchBarHeightValue + FeedLabelHeight
                            else -> {}
                        }
                    }
                }
            ).awaitAll()
        }
    }

    fun onQueryChange(query: String) {
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

    fun addFeed(feedId: String, feedTitle: String) {
        if (query.value.isNotEmpty() && feedsIdWithTitle.all { it.first != feedId }) {
            feedsIdWithTitle.add(feedId to feedTitle)
            if (searchBarState.value == SearchBarState.COLLAPSED) {
                _searchBarState.value = SearchBarState.WITH_FEED
            }
            search()
        }
    }

    fun removeFeed(feedId: String) {
        feedsIdWithTitle.removeAll { it.first == feedId }
        if (feedsIdWithTitle.isEmpty() && searchBarState.value == SearchBarState.WITH_FEED) {
            _searchBarState.value = SearchBarState.COLLAPSED
        }
        search()
    }

    fun onLoading() {
        // isNotEmpty() for handling screen rotation
        if (query.value.isNotEmpty()) searchScreenState = SearchScreenState.Loading
    }

    fun onResultsAvailable() {
        searchScreenState = SearchScreenState.Results
    }

    fun onNothingFound() {
        // isNotEmpty() for handling screen rotation
        if (query.value.isNotEmpty()) searchScreenState = SearchScreenState.NothingFound
    }

    fun onTrendClicked(trendValue: String) {
        _query.value = trendValue
        search()
    }

    fun onSearchBarCloseIconClicked() {
        if (searchBarState.value == SearchBarState.EXPANDED) {
            needClearFocus = true
        }
        else {
            onQueryChange("")
            if (searchScreenState is SearchScreenState.NothingFound) {
                val _trends = trends
                searchScreenState = if (_trends != null)
                    SearchScreenState.Trends(_trends)
                else
                    SearchScreenState.Hint
            }
            feedsIdWithTitle.clear()
            _searchBarState.value = SearchBarState.COLLAPSED
        }
    }

    fun onSearchBarFocusChanged(isFocused: Boolean) {
        when  {
            searchBarState.value == SearchBarState.EXPANDED && !isFocused -> {
                if (searchScreenState is SearchScreenState.EmptyScreen) {
                    val _trends = trends
                    searchScreenState = if (_trends != null)
                        SearchScreenState.Trends(_trends)
                    else
                        SearchScreenState.Hint
                }
                _searchBarState.value = if (feedsIdWithTitle.isNotEmpty())
                    SearchBarState.WITH_FEED
                else
                    SearchBarState.COLLAPSED
            }
            searchBarState.value != SearchBarState.EXPANDED && isFocused -> {
                if (searchScreenState !is SearchScreenState.Results)
                    searchScreenState = SearchScreenState.EmptyScreen
                _searchBarState.value = SearchBarState.EXPANDED
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
        if (searchBarState.value == SearchBarState.EXPANDED) {
            needClearFocus = true
        }
    }

    private fun search() {
        if (_query.value.isNotEmpty()) {
            searchResults = searchTorrentsUseCase(
                query = _query.value,
                sort = sort,
                order = order,
                feeds = feedsIdWithTitle.map { it.first }
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
    COLLAPSED, EXPANDED, WITH_FEED
}
