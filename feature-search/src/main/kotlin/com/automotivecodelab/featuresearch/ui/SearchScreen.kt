package com.automotivecodelab.featuresearch.ui

import android.annotation.SuppressLint
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.automotivecodelab.coreui.ui.ShowErrorSnackbar
import com.automotivecodelab.coreui.ui.SnackbarWithInsets
import com.automotivecodelab.coreui.ui.TorrentCard
import com.automotivecodelab.coreui.ui.injectViewModel
import com.automotivecodelab.coreui.ui.theme.DefaultPadding
import com.automotivecodelab.featuresearch.R
import com.automotivecodelab.featuresearch.di.DaggerSearchComponent
import com.automotivecodelab.featuresearch.di.SearchComponentDeps
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsHeight
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@ExperimentalAnimationApi
@ExperimentalCoroutinesApi
@FlowPreview
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun SearchScreen(
    onMenuItemClick: () -> Unit,
    openDetails: (
        torrentId: String,
        category: String,
        author: String,
        title: String,
        url: String,
    ) -> Unit,
    searchComponentDeps: SearchComponentDeps
) {
    val component = remember {
        DaggerSearchComponent.builder()
            .searchComponentDeps(searchComponentDeps)
            .build()
    }

    val viewmodel: SearchViewModel = injectViewModel { component.searchViewModel() }

    val scaffoldState = rememberScaffoldState()

    viewmodel.error?.ShowErrorSnackbar(scaffoldState)

    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = {
            SnackbarWithInsets(snackbarHostState = it)
        }
    ) {
        val focusManager = LocalFocusManager.current

        val statusBarHeightPx = LocalWindowInsets.current.statusBars.top.toFloat()

        val toolbarHeight = 56.dp /*material guidelines*/ + DefaultPadding * 2

        // scrolling of search bar and lazy list is uncoordinated when using int instead of
        // float
        val toolbarHeightPx = with(LocalDensity.current) {
            toolbarHeight.toPx()
        }

        val toolbarOffsetHeightPx = rememberSaveable {
            mutableStateOf(statusBarHeightPx)
        }

        val nestedScrollConnection = remember {
            object : NestedScrollConnection {
                override fun onPostScroll(
                    consumed: Offset,
                    available: Offset,
                    source: NestedScrollSource
                ): Offset {
                    focusManager.clearFocus()
                    val delta = consumed.y
                    val newOffset = toolbarOffsetHeightPx.value + delta
                    toolbarOffsetHeightPx.value = newOffset.coerceIn(
                        -toolbarHeightPx,
                        statusBarHeightPx
                    )
                    return super.onPostScroll(consumed, available, source)
                }
            }
        }

        val searchResult = viewmodel.searchResults.collectAsLazyPagingItems()

        // didn`t find any other way to show error from pagination library only once.
        // this approach causes bug: if same error happens more than once, snackbar shown only
        // first time
        // todo: should be fixed
        val loadState = searchResult.loadState.refresh
        if (loadState is LoadState.Error &&
            loadState.error.message != viewmodel.error?.peekContent()?.message
        ) {
            LaunchedEffect(key1 = loadState) {
                viewmodel.setError(loadState.error)
            }
        }

        var searchBarState by rememberSaveable { mutableStateOf(SearchBarState.EMPTY) }

        Crossfade(targetState = when {
            loadState is LoadState.Loading ->
                SearchResultState.LOADING
            searchResult.itemCount == 0 && searchBarState == SearchBarState.EMPTY ->
                SearchResultState.START
            searchResult.itemCount == 0 && searchBarState == SearchBarState.WITH_QUERY ->
                SearchResultState.NOTHING_FOUND
            else ->
                SearchResultState.RESULTS
        }) { state ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { focusManager.clearFocus() })
                    }
            ) {
                when (state) {
                    SearchResultState.LOADING -> {
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    }
                    SearchResultState.START -> {
                        Text(
                            text = stringResource(id = R.string.type_to_start_searching),
                            modifier = Modifier.align(Alignment.Center),
                            fontWeight = FontWeight.Light
                        )
                    }
                    SearchResultState.NOTHING_FOUND -> {
                        Text(
                            text = stringResource(id = R.string.nothing_found),
                            modifier = Modifier.align(Alignment.Center),
                            fontWeight = FontWeight.Light
                        )
                    }
                    SearchResultState.RESULTS -> {
                        val statusBarHeightDp = with(LocalDensity.current) {
                            statusBarHeightPx.toDp()
                        }
                        LazyColumn(
                            modifier = Modifier.nestedScroll(nestedScrollConnection),
                            contentPadding = PaddingValues(
                                top = toolbarHeight + statusBarHeightDp
                            ),
                        ) {
                            items(searchResult) { item ->
                                if (item != null) {
                                    TorrentCard(
                                        title = item.title,
                                        updated = item.registered,
                                        author = item.author,
                                        category = item.category,
                                        formattedSize = item.formattedSize,
                                        seeds = item.seeds,
                                        leeches = item.leeches,
                                        // due to paging library architecture, the only way for
                                        // combining two data flows (paging data and favorites) i
                                        // found is to do it right in composable. The reason is
                                        // Flow<PagingData> should be collected only by
                                        // collectAsLazyPagingItems().
                                        // This approach may cause some performance issue. Known
                                        // alternatives of handling data in repository:
                                        // 1. pagerFlow.flatMapLatest{} leads to runtime crash with
                                        // exception from paging library - "pagingData should be
                                        // collected only by collectAsLazyPagingItems()"
                                        // 2. favorites.flatMapLatest{} leads to data been reloaded
                                        // every time favorites emits. Best alternative
                                        // 3. favorites.first() will be reacting to changes only
                                        // after data reload
                                        isFavorite = viewmodel.favorites.collectAsState().value
                                            .any { it.torrentId == item.id }
                                    ) {
                                        focusManager.clearFocus()
                                        openDetails(
                                            item.id,
                                            item.category,
                                            item.author,
                                            item.title,
                                            item.url
                                        )
                                    }
                                    Divider()
                                }
                            }
                            item {
                                Spacer(
                                    Modifier
                                        .fillMaxWidth()
                                        .navigationBarsHeight()
                                )
                            }
                        }
                    }
                }
            }
        }

        SearchBar(
            toolbarOffsetHeightPx = toolbarOffsetHeightPx,
            searchBarState = searchBarState,
            onSearchBarStateChange = { searchBarState = it },
            viewModel = viewmodel,
            onMenuItemClick = onMenuItemClick,
            clearFocus = { focusManager.clearFocus() }
        )
    }
}

enum class SearchResultState {
    START, LOADING, NOTHING_FOUND, RESULTS
}
