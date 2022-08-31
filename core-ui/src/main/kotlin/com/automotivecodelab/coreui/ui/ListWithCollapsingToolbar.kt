package com.automotivecodelab.coreui.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun <T> ListWithCollapsingToolbar(
    items: List<T>,
    itemComposable: @Composable (T) -> Unit,
    toolbarText: String,
    toolbarColor: Color,
    navigationIcon: @Composable () -> Unit,
    isLoading: Boolean
) {
    val statusBarHeightPx = WindowInsets.statusBars.getTop(LocalDensity.current).toFloat()

    val toolbarHeight = 56.dp

    // scrolling of search bar and lazy list is asynchronous when using int instead of float
    val toolbarHeightPx = with(LocalDensity.current) {
        toolbarHeight.toPx()
    }
    var toolbarOffsetHeightPx by rememberSaveable {
        mutableStateOf(0f)
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            // when using onPreScroll ("available" offset), toolbar is moving even when the list is
            // shorter than screen height and no scrolling actually happens
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                val delta = consumed.y
                val newOffset = toolbarOffsetHeightPx + delta
                toolbarOffsetHeightPx = newOffset.coerceIn(
                    -(toolbarHeightPx + statusBarHeightPx),
                    0f
                )
                return super.onPostScroll(consumed, available, source)
            }
        }
    }

    Crossfade(targetState = isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (it) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            } else {
                val statusBarHeightDp = with(LocalDensity.current) {
                    statusBarHeightPx.toDp()
                }
                LazyColumn(
                    modifier = Modifier.nestedScroll(nestedScrollConnection),
                    contentPadding = PaddingValues(top = toolbarHeight + statusBarHeightDp)
                ) {
                    items(items) { item ->
                        itemComposable(item)
                    }
                    item {
                        Spacer(
                            modifier = Modifier
                                .windowInsetsBottomHeight(WindowInsets.navigationBars)
                        )
                    }
                }
            }
        }
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .offset {
                IntOffset(x = 0, y = toolbarOffsetHeightPx.roundToInt())
            },
        elevation = AppBarDefaults.TopAppBarElevation
    ) {
        Column {
            Spacer(
                modifier = Modifier
                    .windowInsetsTopHeight(WindowInsets.statusBars)
                    .background(Color.Transparent)
            )
            TopAppBar(
                title = {
                    Text(toolbarText, maxLines = 1)
                },
                navigationIcon = { navigationIcon() },
                backgroundColor = toolbarColor,
                elevation = 0.dp
            )
        }
    }
}
