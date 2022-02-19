package com.automotivecodelab.rtclient.ui

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.automotivecodelab.coreui.ui.Event
import com.automotivecodelab.coreui.ui.SnackbarWithInsets
import com.automotivecodelab.coreui.ui.theme.DefaultCornerRadius
import com.automotivecodelab.coreui.ui.theme.RTClientTheme
import com.automotivecodelab.featurerssfeeds.ui.rssentriesscreen.RssEntriesScreen
import com.automotivecodelab.featurerssfeeds.ui.rssfeedsscreen.RssFeedsScreen
import com.automotivecodelab.featuresearch.ui.SearchScreen
import com.automotivecodelab.rtclient.AppComponent
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.statusBarsHeight
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

@FlowPreview
@ExperimentalCoroutinesApi
@ExperimentalAnimationApi
@DelicateCoroutinesApi
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun HostScreen(
    deepLinkEvent: Event<Uri>?
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val navController = rememberAnimatedNavController()
    val onMenuItemClick = {
        scope.launch {
            scaffoldState.drawerState.apply {
                if (isClosed) open() else close()
            }
        }
    }

    if (deepLinkEvent != null && !deepLinkEvent.hasBeenHandled) {
        navController.navigate(deepLinkEvent.getContent())
    }

    val systemUiController = rememberSystemUiController()
    val context = LocalContext.current
    val themeStore = remember { CurrentAppThemeSource(context) }
    val initialTheme = remember { runBlocking { themeStore.appTheme.first() } }
    val theme by themeStore.appTheme.collectAsState(initial = initialTheme)
    val isSystemInDarkTheme = isSystemInDarkTheme()
    val darkMode = remember(theme) {
        when (theme) {
            AppTheme.LIGHT -> false
            AppTheme.DARK -> true
            AppTheme.AUTO -> isSystemInDarkTheme
        }
    }

    CircularReveal(targetState = darkMode, animationSpec = tween(500)) { isDarkTheme ->
        RTClientTheme(isDarkTheme) {
            LaunchedEffect(key1 = isDarkTheme, block = {
                systemUiController.setSystemBarsColor(
                    color = Color.Transparent,
                    darkIcons = !isDarkTheme,
                    isNavigationBarContrastEnforced = false,
                )
            })

            Scaffold(
                snackbarHost = {
                    SnackbarWithInsets(snackbarHostState = it)
                },
                drawerGesturesEnabled = true,
                scaffoldState = scaffoldState,
                drawerShape = RoundedCornerShape(
                    topEnd = DefaultCornerRadius,
                    bottomEnd = DefaultCornerRadius
                ),
                drawerContent = {
                    Spacer(modifier = Modifier.statusBarsHeight())
                    val currentBackStackEntry =
                        navController.currentBackStackEntryAsState()
                    DrawerItem(
                        painter = painterResource(id = Screen.Search.icon!!),
                        text = stringResource(id = Screen.Search.label!!),
                        onClick = {
                            scope.launch {
                                scaffoldState.drawerState.close()
                                navController.navigate(Screen.Search.routeId) {
                                    popUpTo(navController.graph.startDestinationId)
                                    // bug: incorrect navigation through deeplink when savestate = true:
                                    // no action when click on search button in nav drawer
                                    // https://stackoverflow.com/questions/68456471/jetpack-compose-bottom-bar-navigation-not-responding-after-deep-linking
//                            {
//                                saveState = true
//                            }
                                    launchSingleTop = true
                                    // restoreState = true
                                }
                            }
                        },
                        isSelected = currentBackStackEntry.value?.destination?.route ==
                            Screen.Search.routeId
                    )
                    DrawerItem(
                        painter = painterResource(id = Screen.Feeds.icon!!),
                        text = stringResource(id = Screen.Feeds.label!!),
                        onClick = {
                            scope.launch {
                                scaffoldState.drawerState.close()
                                navController.navigate(Screen.Feeds.routeId) {
                                    popUpTo(navController.graph.startDestinationId)
//                            {
//                                saveState = true
//                            }
                                    launchSingleTop = true
                                    // restoreState = true
                                }
                            }
                        },
                        isSelected = currentBackStackEntry.value?.destination?.route ==
                            Screen.Feeds.routeId ||
                            currentBackStackEntry.value?.destination?.route ==
                            Screen.FeedEntries.routeId
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    ThemeSelector(theme = theme, onThemeChanged = {
                        scope.launch { themeStore.saveTheme(it) }
                    })
                    Spacer(modifier = Modifier.navigationBarsHeight())
                }
            ) {
                BackHandler(enabled = scaffoldState.drawerState.isOpen) {
                    scope.launch { scaffoldState.drawerState.close() }
                }

                AnimatedNavHost(
                    navController = navController,
                    startDestination = Screen.Search.routeId
                ) {
                    composable(Screen.Search.routeId) {
                        SearchScreen(
                            onMenuItemClick = { onMenuItemClick() },
                            searchComponentDeps = LocalContext.current.AppComponent,
                            detailsComponentDeps = LocalContext.current.AppComponent,
                            navigateToFeed = { title, threadId ->
                                navController.navigate(
                                    Screen.FeedEntries.routeConstructor(
                                        threadId, title, torrentIdToOpen = null
                                    )
                                )
                            },
                            isDarkMode = isDarkTheme
                        )
                    }
                    composable(
                        route = Screen.Feeds.routeId,
                        enterTransition = { initial, _ ->
                            when (initial.destination.route) {
                                Screen.FeedEntries.routeId ->
                                    slideIntoContainer(AnimatedContentScope.SlideDirection.Right)
                                else -> EnterTransition.None
                            }
                        },
                        exitTransition = { _, target ->
                            when (target.destination.route) {
                                Screen.FeedEntries.routeId ->
                                    slideOutOfContainer(AnimatedContentScope.SlideDirection.Left)
                                else -> fadeOut()
                            }
                        }
                    ) {
                        RssFeedsScreen(
                            onMenuItemClick = { onMenuItemClick() },
                            navigateToFeedEntriesScreen = { title, threadId ->
                                navController.navigate(
                                    Screen.FeedEntries.routeConstructor(
                                        threadId, title, torrentIdToOpen = null
                                    )
                                )
                            },
                            rssFeedsDeps = LocalContext.current.AppComponent,
                            detailsComponentDeps = LocalContext.current.AppComponent
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
                        enterTransition = { initial, _ ->
                            when (initial.destination.route) {
                                Screen.Feeds.routeId ->
                                    slideIntoContainer(AnimatedContentScope.SlideDirection.Left)
                                else -> fadeIn()
                            }

                        },
                        exitTransition = { _, target ->
                            when (target.destination.route) {
                                Screen.Feeds.routeId ->
                                    slideOutOfContainer(AnimatedContentScope.SlideDirection.Right)
                                else -> fadeOut()
                            }
                        },

                    ) { backStackEntry ->
                        val threadId = backStackEntry.arguments?.getString(
                            Screen.FeedEntries.THREAD_ID
                        )
                        val title = backStackEntry.arguments?.getString(Screen.FeedEntries.TITLE)
                        val torrentIdToOpen = backStackEntry.arguments?.getString(
                            Screen.FeedEntries.TORRENT_ID
                        )
                        RssEntriesScreen(
                            title = title!!,
                            threadId = threadId!!,
                            torrentId = torrentIdToOpen,
                            navigateUp = navController::navigateUp,
                            rssFeedsDeps = LocalContext.current.AppComponent,
                            detailsComponentDeps = LocalContext.current.AppComponent,
                            scaffoldState = scaffoldState,
                            coroutineScope = scope,
                            isDarkMode = isDarkTheme
                        )
                    }
                }
            }
        }
    }
}
