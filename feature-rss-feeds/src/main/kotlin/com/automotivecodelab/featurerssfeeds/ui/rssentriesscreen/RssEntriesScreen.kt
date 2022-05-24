package com.automotivecodelab.featurerssfeeds.ui.rssentriesscreen

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import com.automotivecodelab.coreui.ui.*
import com.automotivecodelab.featurerssfeeds.di.DaggerRssFeedsComponent
import com.automotivecodelab.featurerssfeeds.di.RssFeedsDeps
import com.automotivecodelab.featurerssfeeds.domain.models.RssEntriesLoadingResult
import kotlinx.coroutines.CoroutineScope

@ExperimentalMaterialApi
@Composable
fun RssEntriesScreen(
    threadName: String,
    threadId: String,
    torrentId: String?,
    navigateUp: () -> Unit,
    openDetails: (
        torrentId: String,
        category: String,
        author: String,
        title: String,
        url: String,
    ) -> Unit,
    rssFeedsDeps: RssFeedsDeps,
    scaffoldState: ScaffoldState,
    coroutineScope: CoroutineScope,
) {
    val component = remember {
        DaggerRssFeedsComponent.builder()
            .rssFeedsDeps(rssFeedsDeps)
            .build()
    }

    val viewmodel: RssEntriesViewModel = injectViewModel {
        component.rssEntriesViewModelFactory().create(threadId = threadId, torrentId = torrentId)
    }

    viewmodel.openDetailsEvent?.let {
        if (!it.hasBeenHandled) {
            val rssChannelEntry = it.getContent()
            openDetails(
                rssChannelEntry.id,
                threadName,
                rssChannelEntry.author,
                rssChannelEntry.title,
                rssChannelEntry.link
            )
        }
    }

    val entriesLoadingValue = viewmodel.entries.collectAsState().value
    if (entriesLoadingValue is RssEntriesLoadingResult.Error) {
        if (entriesLoadingValue.t != null) {
            Event(entriesLoadingValue.t).ShowErrorSnackbar(
                scaffoldState = scaffoldState,
                coroutineScope = coroutineScope
            )
        }
        LaunchedEffect(key1 = true, block = {
            navigateUp()
        })
    }

    ListWithCollapsingToolbar(
        items = if (entriesLoadingValue is RssEntriesLoadingResult.Success)
            entriesLoadingValue.data else emptyList(),
        itemComposable = {
            TorrentCard(
                title = it.title,
                updated = it.updated,
                author = it.author,
                category = null,
                formattedSize = null,
                seeds = null,
                leeches = null,
                isFavorite = it.isFavorite
            ) {
                openDetails(
                    it.id,
                    threadName,
                    it.author,
                    it.title,
                    it.link
                )
            }
            Divider()
        },
        navigationIcon = {
            IconButton(onClick = navigateUp) {
                Icon(
                    Icons.Filled.ArrowBack,
                    null,
                    tint = MaterialTheme.colors.onSurface
                )
            }
        },
        toolbarColor = MaterialTheme.colors.surface,
        toolbarText = threadName,
        isLoading = entriesLoadingValue is RssEntriesLoadingResult.Loading
    )
}
