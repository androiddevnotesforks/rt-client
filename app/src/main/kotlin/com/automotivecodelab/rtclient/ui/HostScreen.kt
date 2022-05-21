package com.automotivecodelab.rtclient.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.*
import com.automotivecodelab.coreui.ui.Event
import com.automotivecodelab.coreui.ui.SnackbarWithInsets
import com.automotivecodelab.coreui.ui.theme.DefaultCornerRadius
import com.automotivecodelab.coreui.ui.theme.RTClientTheme
import com.automotivecodelab.rtclient.appComponent
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.statusBarsHeight
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.*
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterialNavigationApi::class)
@FlowPreview
@ExperimentalCoroutinesApi
@ExperimentalAnimationApi
@DelicateCoroutinesApi
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun HostScreen(
    deepLinkEvent: Event<Intent>?
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    val bottomSheetNavigator = remember(modalBottomSheetState) {
        BottomSheetNavigator(sheetState = modalBottomSheetState)
    }
    val navController = rememberAnimatedNavController(bottomSheetNavigator)
    val onMenuItemClick = {
        scope.launch {
            scaffoldState.drawerState.apply {
                if (isClosed) open() else close()
            }
        }
        Unit
    }

    val systemUiController = rememberSystemUiController()
    val context = LocalContext.current
    val themeStore = remember { context.appComponent.appThemeSource() }
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
                    val currentScreenRouteId by navController.currentScreenRouteId().collectAsState(
                        initial = Screen.Search.routeId
                    )
                    DrawerItem(
                        painter = painterResource(id = Screen.Search.icon!!),
                        text = stringResource(id = Screen.Search.label!!),
                        onClick = {
                            scope.launch {
                                scaffoldState.drawerState.close()
                                navController.navigate(SEARCH_GRAPH_ROUTE) {
                                    popUpTo(Screen.Search.routeId)
                                        {
                                            saveState = true
                                        }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        isSelected = currentScreenRouteId == Screen.Search.routeId
                    )
                    DrawerItem(
                        painter = painterResource(id = Screen.Feeds.icon!!),
                        text = stringResource(id = Screen.Feeds.label!!),
                        onClick = {
                            scope.launch {
                                scaffoldState.drawerState.close()
                                navController.navigate(RSS_FEEDS_GRAPH_ROUTE) {
                                    popUpTo(Screen.Search.routeId)
                                        {
                                            saveState = true
                                        }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        isSelected = currentScreenRouteId == Screen.Feeds.routeId ||
                                currentScreenRouteId == Screen.FeedEntries.routeId
                    )
                    DrawerItem(
                        painter = painterResource(id = Screen.Favorites.icon!!),
                        text = stringResource(id = Screen.Favorites.label!!),
                        onClick = {
                            scope.launch {
                                scaffoldState.drawerState.close()
                                navController.navigate(FAVORITES_GRAPH_ROUTE) {
                                    popUpTo(Screen.Search.routeId)
                                        {
                                            saveState = true
                                        }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        isSelected = currentScreenRouteId == Screen.Favorites.routeId

                    )
                    Spacer(modifier = Modifier.weight(1f))
                    ThemeSelector(theme = theme, onThemeChanged = {
                        scope.launch { themeStore.saveTheme(it) }
                    })
                    Spacer(modifier = Modifier.navigationBarsHeight())
                }
            ) {
                ModalBottomSheetLayout(
                    bottomSheetNavigator = bottomSheetNavigator,
                    sheetShape = RoundedCornerShape(
                        topStart = DefaultCornerRadius,
                        topEnd = DefaultCornerRadius
                    ),
                    sheetBackgroundColor = MaterialTheme.colors.background,
                    sheetElevation = if (isDarkTheme) 0.dp else ModalBottomSheetDefaults.Elevation,
                ) {
                    BackHandler(enabled = modalBottomSheetState.isVisible ||
                            scaffoldState.drawerState.isOpen
                    ) {
                        if (scaffoldState.drawerState.isOpen)
                            scope.launch { scaffoldState.drawerState.close() }
                        else if (modalBottomSheetState.isVisible)
                            scope.launch { modalBottomSheetState.hide() }
                    }
                    AnimatedNavHost(
                        navController = navController,
                        startDestination = SEARCH_GRAPH_ROUTE
                    ) {
                        searchGraph(
                            navController = navController,
                            onMenuItemClick = onMenuItemClick
                        )
                        rssFeedsGraph(
                            navController = navController,
                            onMenuItemClick = onMenuItemClick,
                            scope = scope,
                            scaffoldState = scaffoldState
                        )
                        favoritesGraph(
                            navController = navController,
                            onMenuItemClick = onMenuItemClick
                        )
                        torrentDetailsBottomSheet(navController)
                    }
                }
            }
        }
    }

    if (deepLinkEvent != null && !deepLinkEvent.hasBeenHandled) {
        val intent = deepLinkEvent.getContent()
        // workaround for bug when clicking on notification when app is in foreground causes
        // activity to recreate. The problem is in intent flag FLAG_ACTIVITY_NEW_TASK, which setted
        // implicitly somewhere, probably on step of wrapping Intent to PendingIntent. Using
        // navigate() instead of handleDeepLink() causes much more stranger bug:
        // https://stackoverflow.com/questions/68456471/jetpack-compose-bottom-bar-navigation-not-responding-after-deep-linking
        intent.flags = 0
        navController.handleDeepLink(intent)
    }
}

fun NavController.currentScreenRouteId(): Flow<String> {
    return currentBackStackEntryFlow
        .map {
            if (it.destination.route == Screen.Details.routeId)
                previousBackStackEntry?.destination?.route
            else
                it.destination.route
        }
        .filterNotNull()
}

