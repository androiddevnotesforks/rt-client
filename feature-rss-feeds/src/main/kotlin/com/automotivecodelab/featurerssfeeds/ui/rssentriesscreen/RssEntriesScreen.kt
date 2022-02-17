package com.automotivecodelab.featurerssfeeds.ui.rssentriesscreen

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.automotivecodelab.coreui.ui.*
import com.automotivecodelab.featuredetailsbottomsheet.di.DaggerDetailsComponent
import com.automotivecodelab.featuredetailsbottomsheet.di.DetailsComponentDeps
import com.automotivecodelab.featuredetailsbottomsheet.ui.BottomSheetDetailsLayout
import com.automotivecodelab.featurerssfeeds.di.DaggerRssFeedsComponent
import com.automotivecodelab.featurerssfeeds.di.RssFeedsDeps
import kotlinx.coroutines.CoroutineScope

@ExperimentalMaterialApi
@Composable
fun RssEntriesScreen(
    title: String,
    threadId: String,
    torrentId: String?,
    navigateUp: () -> Unit,
    rssFeedsDeps: RssFeedsDeps,
    detailsComponentDeps: DetailsComponentDeps,
    scaffoldState: ScaffoldState,
    coroutineScope: CoroutineScope,
    isDarkMode: Boolean
) {
    val component = remember {
        DaggerRssFeedsComponent.builder()
            .rssFeedsDeps(rssFeedsDeps)
            .detailsComponent(
                DaggerDetailsComponent.builder()
                    .detailsComponentDeps(detailsComponentDeps)
                    .build()
            )
            .build()
    }

    val viewmodel: RssEntriesViewModel = injectViewModel {
        component.rssEntriesViewModelFactory().create(threadId, torrentId)
    }

    viewmodel.error?.ShowErrorSnackbar(
        scaffoldState = scaffoldState,
        coroutineScope = coroutineScope
    )

    viewmodel.closeScreenEvent?.let {
        if (!it.hasBeenHandled) {
            it.getContent()
            navigateUp()
        }
    }

    BottomSheetDetailsLayout(
        viewModel = viewmodel,
        navigateToFeed = { _, _ -> },
        isDarkMode = isDarkMode
    ) {
        ListWithCollapsingToolbar(
            items = viewmodel.entries,
            itemComposable = {
                TorrentCard(
                    title = it.title,
                    updated = it.updated,
                    author = it.author,
                    category = null,
                    formattedSize = null,
                    seeds = null,
                    leeches = null
                ) {
                    viewmodel.openDetails(
                        author = it.author,
                        category = null,
                        title = it.title,
                        torrentId = it.id,
                        url = it.link
                    )
                }
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
            toolbarText = title,
            isLoading = viewmodel.isLoading
        )
    }
}
