package com.automotivecodelab.featurefavoritesimpl.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.automotivecodelab.coreui.ui.TorrentCard

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FavoriteCard(
    favorite: FavoriteUIModel,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    val dismissState = rememberDismissState()
    if (dismissState.isDismissed(DismissDirection.EndToStart)) {
        onDelete()
    }
    SwipeToDismiss(
        state = dismissState,
        dismissThresholds = { FractionalThreshold(0.5f) },
        background = {
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    DismissValue.Default -> MaterialTheme.colors.background
                    DismissValue.DismissedToStart -> MaterialTheme.colors.secondaryVariant
                    else -> MaterialTheme.colors.background
                }
            )

            val iconTint by animateColorAsState(
                when (dismissState.targetValue) {
                    DismissValue.Default -> MaterialTheme.colors.onBackground
                    DismissValue.DismissedToStart -> MaterialTheme.colors.background
                    else -> MaterialTheme.colors.background
                }
            )
            val scale by animateFloatAsState(
                if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    modifier = Modifier.scale(scale),
                    tint = iconTint
                )
            }
        },
        directions = setOf(DismissDirection.EndToStart)
    ) {
        Surface(
            elevation =
            animateDpAsState(if (dismissState.dismissDirection != null) 4.dp else 0.dp).value,
            modifier = Modifier
                .fillMaxWidth(),
            color = MaterialTheme.colors.background
        ) {
            TorrentCard(
                title = favorite.title,
                updated = null,
                author = favorite.author,
                category = favorite.category,
                formattedSize = null,
                seeds = null,
                leeches = null,
                onClick = onClick,
                isFavorite = null
            )
        }
    }
    Divider()
}
