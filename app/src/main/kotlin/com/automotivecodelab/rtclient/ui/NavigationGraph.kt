package com.automotivecodelab.rtclient.ui

import androidx.compose.animation.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ScaffoldState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.*
import com.automotivecodelab.featuredetails.ui.TorrentDetails
import com.automotivecodelab.featurefavoritesimpl.ui.FavoritesScreen
import com.automotivecodelab.featurerssfeeds.ui.rssentriesscreen.RssEntriesScreen
import com.automotivecodelab.featurerssfeeds.ui.rssfeedsscreen.RssFeedsScreen
import com.automotivecodelab.featuresearch.ui.SearchScreen
import com.automotivecodelab.rtclient.appComponent
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi

const val SEARCH_GRAPH_ROUTE = "search_graph_route"
const val RSS_FEEDS_GRAPH_ROUTE = "rss_feeds_graph_route"
const val FAVORITES_GRAPH_ROUTE = "favorites_graph_route"

@OptIn(
    ExperimentalAnimationApi::class,
    ExperimentalMaterialNavigationApi::class,
    ExperimentalMaterialApi::class,
    ExperimentalComposeUiApi::class,
    ExperimentalCoroutinesApi::class,
    kotlinx.coroutines.FlowPreview::class
)
fun NavGraphBuilder.searchGraph(
    navController: NavController,
    onMenuItemClick: () -> Unit
) {
    navigation(
        startDestination = Screen.Search.routeId,
        route = SEARCH_GRAPH_ROUTE
    ) {
        composable(Screen.Search.routeId) {
            SearchScreen(
                onMenuItemClick = onMenuItemClick,
                openDetails = { torrentId: String, category: String, author: String, title: String,
                                url: String ->
                    navController.navigate(
                        route = Screen.Details.routeConstructor(
                            torrentId = torrentId,
                            category = category,
                            author = author,
                            title = title,
                            url = url
                        )
                    )
                },
                searchComponentDeps = LocalContext.current.appComponent,
            )
        }
        //torrentDetailsBottomSheet(navController)
    }
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
fun NavGraphBuilder.rssFeedsGraph(
    navController: NavController,
    onMenuItemClick: () -> Unit,
    scope: CoroutineScope,
    scaffoldState: ScaffoldState
) {
    navigation(
        startDestination = Screen.Feeds.routeId,
        route = RSS_FEEDS_GRAPH_ROUTE
    ) {
        composable(
            route = Screen.Feeds.routeId,
            enterTransition = {
                when (initialState.destination.route) {
                    Screen.FeedEntries.routeId ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right
                        )
                    else -> EnterTransition.None
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Screen.FeedEntries.routeId ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Left
                        )
                    else -> fadeOut()
                }
            }
        ) {
            RssFeedsScreen(
                onMenuItemClick = onMenuItemClick,
                navigateToFeedEntriesScreen = { title, threadId ->
                    navController.navigate(
                        Screen.FeedEntries.routeConstructor(
                            threadId, title, torrentIdToOpen = null
                        )
                    )
                },
                rssFeedsDeps = LocalContext.current.appComponent
            )
        }
        composable(
            route = Screen.FeedEntries.routeId,
            arguments = listOf(
                navArgument(Screen.FeedEntries.THREAD_ID) { nullable = true },
                navArgument(Screen.FeedEntries.TITLE) { nullable = true },
                navArgument(Screen.FeedEntries.TORRENT_ID) { nullable = true }
            ),
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "${Screen.URI}/${Screen.FeedEntries.routeId}"
                }
            ),
            enterTransition = {
                when (initialState.destination.route) {
                    Screen.Feeds.routeId ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left
                        )
                    else -> fadeIn()
                }

            },
            exitTransition = {
                when (targetState.destination.route) {
                    Screen.Feeds.routeId ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right
                        )
                    else -> fadeOut()
                }
            },

            ) { backStackEntry ->
            val threadId = backStackEntry.arguments?.getString(
                Screen.FeedEntries.THREAD_ID
            )
            val title = backStackEntry.arguments?.getString(
                Screen.FeedEntries.TITLE)
            val torrentIdToOpen = backStackEntry.arguments?.getString(
                Screen.FeedEntries.TORRENT_ID
            )
            RssEntriesScreen(
                threadName = title!!,
                threadId = threadId!!,
                navigateUp = navController::navigateUp,
                rssFeedsDeps = LocalContext.current.appComponent,
                scaffoldState = scaffoldState,
                coroutineScope = scope,
                openDetails = { torrentId: String, category: String, author: String, _title: String,
                                url: String ->
                    navController.navigate(
                        route = Screen.Details.routeConstructor(
                            torrentId = torrentId,
                            category = category,
                            author = author,
                            title = _title,
                            url = url
                        )
                    )
                },
                torrentId = torrentIdToOpen
            )
        }
        //torrentDetailsBottomSheet(navController)
    }
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.favoritesGraph(
    navController: NavController,
    onMenuItemClick: () -> Unit
) {
    navigation(
        startDestination = Screen.Favorites.routeId,
        route = FAVORITES_GRAPH_ROUTE
    ) {
        composable(Screen.Favorites.routeId) {
            FavoritesScreen(
                onMenuItemClick = onMenuItemClick,
                favoritesComponentDeps = LocalContext.current.appComponent,
                openDetails = { torrentId: String, category: String, author: String, title: String,
                                url: String ->
                    navController.navigate(
                        route = Screen.Details.routeConstructor(
                            torrentId = torrentId,
                            category = category,
                            author = author,
                            title = title,
                            url = url
                        )
                    )
                },
            )
        }
        //torrentDetailsBottomSheet(navController)
    }
}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.torrentDetailsBottomSheet(navController: NavController) {
    bottomSheet(
        route = Screen.Details.routeId,
        arguments = listOf(
            navArgument(Screen.Details.TORRENT_ID) { nullable = true },
            navArgument(Screen.Details.CATEGORY) { nullable = true },
            navArgument(Screen.Details.AUTHOR) { nullable = true },
            navArgument(Screen.Details.TITLE) { nullable = true },
            navArgument(Screen.Details.URL) { nullable = true },
        ),
    ) { backStackEntry ->
        val torrentId = backStackEntry.arguments?.getString(
            Screen.Details.TORRENT_ID)
        val category = backStackEntry.arguments?.getString(
            Screen.Details.CATEGORY)
        val author = backStackEntry.arguments?.getString(
            Screen.Details.AUTHOR)
        val title = backStackEntry.arguments?.getString(
            Screen.Details.TITLE)
        val url = backStackEntry.arguments?.getString(
            Screen.Details.URL)
        TorrentDetails(
            torrentId = torrentId!!,
            category = category!!,
            author = author!!,
            title = title!!,
            url = url!!,
            navigateToFeed = { threadName: String, threadId: String ->
                navController.navigate(
                    Screen.FeedEntries.routeConstructor(
                        threadId, threadName, torrentIdToOpen = null
                    )
                )
            },
            detailsDeps = LocalContext.current.appComponent
        )
    }
}