package com.automotivecodelab.featurefavoritesimpl.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.automotivecodelab.coreui.ui.SnackbarWithInsets
import com.automotivecodelab.coreui.ui.injectViewModel
import com.automotivecodelab.featurefavoritesimpl.di.FavoritesComponentDeps
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.statusBarsHeight
import com.automotivecodelab.featurefavoritesimpl.R
import com.automotivecodelab.featurefavoritesimpl.di.DaggerFavoritesComponent

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FavoritesScreen(
    onMenuItemClick: () -> Unit,
    openDetails: (
        torrentId: String,
        category: String,
        author: String,
        title: String,
        url: String,
    ) -> Unit,
    favoritesComponentDeps: FavoritesComponentDeps,
) {
    val component = remember {
        DaggerFavoritesComponent.builder()
            .favoritesComponentDeps(favoritesComponentDeps)
            .build()
    }

    val viewmodel: FavoritesViewModel = injectViewModel {
        component.favoritesViewModel()
    }

    val scaffoldState = rememberScaffoldState()

    Scaffold(
        snackbarHost = {
            SnackbarWithInsets(snackbarHostState = it)
        },
        scaffoldState = scaffoldState,
        topBar = {
            Surface(elevation = AppBarDefaults.TopAppBarElevation) {
                Column {
                    Surface(
                        modifier = Modifier
                            .statusBarsHeight()
                            .fillMaxWidth(),
                        color = Color.Transparent,
                        elevation = 0.dp
                    ) {}
                    TopAppBar(
                        title = { Text(stringResource(id = R.string.favorites)) },
                        navigationIcon = {
                            IconButton(onClick = onMenuItemClick) {
                                Icon(
                                    Icons.Filled.Menu,
                                    null,
                                    tint = MaterialTheme.colors.onSurface
                                )
                            }
                        },
                        backgroundColor = MaterialTheme.colors.surface,
                        elevation = 0.dp
                    )
                }
            }
        }
    ) {
        val favorites = viewmodel.favorites.collectAsState()
        Crossfade(targetState = favorites.value.isNotEmpty()) {
            if (it) {
                LazyColumn {
                    items(
                        items = favorites.value,
                    ) { favorite ->
                        FavoriteCard(
                            favorite = favorite,
                            onDelete = { viewmodel.deleteFavorite(favorite) },
                            onClick = {
                                openDetails(
                                    favorite.torrentId,
                                    favorite.category,
                                    favorite.author,
                                    favorite.title,
                                    favorite.url
                                )
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.navigationBarsHeight())
                    }
                }
            }
        }
    }
}