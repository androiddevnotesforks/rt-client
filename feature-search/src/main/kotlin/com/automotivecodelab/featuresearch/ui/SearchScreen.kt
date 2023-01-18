package com.automotivecodelab.featuresearch.ui

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.automotivecodelab.coreui.ui.*
import com.automotivecodelab.coreui.ui.theme.DefaultPadding
import com.automotivecodelab.featuresearch.R
import com.automotivecodelab.featuresearch.di.DaggerSearchComponent
import com.automotivecodelab.featuresearch.di.SearchComponentDeps
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

    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = {
            SnackbarWithInsets(snackbarHostState = it)
        }
    ) {
        val focusManager = LocalFocusManager.current

        LaunchedEffect(key1 = viewmodel.needClearFocus, block = {
            if (viewmodel.needClearFocus) {
                focusManager.clearFocus()
                viewmodel.onFocusCleared()
            }
        })

        val statusBarHeightPx = WindowInsets.statusBars.getTop(LocalDensity.current).toFloat()

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
                    viewmodel.onScroll()
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

        val searchResult = viewmodel.searchResults?.collectAsLazyPagingItems()

        // bug: snackbar appears every time when we re-enter the screen
        val loadState = searchResult?.loadState?.refresh
        if (loadState is LoadState.Error) {
            Event(loadState.error).ShowErrorSnackbar(scaffoldState = scaffoldState)
        }

        LaunchedEffect(key1 = loadState, key2 = searchResult) {
            when {
                loadState is LoadState.Loading ->
                    viewmodel.onLoading()
                searchResult != null && searchResult.itemCount > 0 ->
                    viewmodel.onResultsAvailable()
                searchResult?.itemCount == 0 ->
                    viewmodel.onNothingFound()
            }
        }

        Crossfade(
            modifier = Modifier
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { viewmodel.clearFocus() })
                },
            targetState = viewmodel.searchScreenState
        ) { state ->
            Box(modifier = Modifier.fillMaxSize()) {
                when (state) {
                    is SearchScreenState.Loading -> {
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    }
                    is SearchScreenState.EmptyScreen -> {  }
                    is SearchScreenState.Hint -> {
                        Text(
                            text = stringResource(id = R.string.type_to_start_searching),
                            modifier = Modifier.align(Alignment.Center),
                            fontWeight = FontWeight.Light
                        )
                    }
                    is SearchScreenState.Trends -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            verticalArrangement = Arrangement.spacedBy(DefaultPadding),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (LocalConfiguration.current.orientation ==
                                Configuration.ORIENTATION_LANDSCAPE
                            ) {
                                Spacer(
                                    modifier = Modifier
                                        .height(toolbarHeight)
                                        .statusBarsPadding()
                                )
                            } else {
                                Text(
                                    text = stringResource(id = R.string.trendingQueries),
                                    fontWeight = FontWeight.Light
                                )
                                Spacer(
                                    modifier = Modifier.height(DefaultPadding)
                                )
                            }
                            state.trends.forEach {
                                Text(
                                    text = it,
                                    fontWeight = FontWeight.Light,
                                    modifier = Modifier.clickable { viewmodel.onTrendClicked(it) }
                                )
                            }
                        }
                    }
                    SearchScreenState.NothingFound -> {
                        Text(
                            text = stringResource(id = R.string.nothing_found),
                            modifier = Modifier.align(Alignment.Center),
                            fontWeight = FontWeight.Light
                        )
                    }
                    SearchScreenState.Results -> {
                        val statusBarHeightDp = with(LocalDensity.current) {
                            statusBarHeightPx.toDp()
                        }
                        LazyColumn(
                            modifier = Modifier.nestedScroll(nestedScrollConnection),
                            contentPadding = PaddingValues(
                                top = toolbarHeight + statusBarHeightDp
                            ),
                        ) {
                            items(
                                searchResult ?: error("searchResult is null")
                            ) { item ->
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
                                            .any { it.torrentId == item.id },
                                        onClickCategory = {
                                            viewmodel.onFeedSelected(
                                                feedId = item.categoryId,
                                                feedTitle = item.category
                                            )
                                        }
                                    ) {
                                        viewmodel.clearFocus()
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
                                        .windowInsetsBottomHeight(WindowInsets.navigationBars)
                                )
                            }
                        }
                    }
                }
            }
        }

        SearchBar(
            toolbarOffsetHeightPx = toolbarOffsetHeightPx,
            viewModel = viewmodel,
            onMenuItemClick = onMenuItemClick,
        )
    }
}

