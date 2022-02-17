package com.automotivecodelab.featurerssfeeds.ui.rssfeedsscreen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.automotivecodelab.coreui.ui.theme.DefaultPadding

@ExperimentalMaterialApi
@Composable
fun RssFeedCard(
    title: String,
    link: String,
    isSubscribed: Boolean,
    onToggleSubscription: (Boolean) -> Unit,
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
                .fillMaxWidth()
                .clickable(onClick = onClick),
            color = MaterialTheme.colors.background
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(DefaultPadding),
                    modifier = Modifier
                        .padding(DefaultPadding)
                        .weight(7f)
                ) {
                    Text(
                        text = title,
                        color = MaterialTheme.colors.primary
                    )
                    Text(link, style = MaterialTheme.typography.caption)
                }
                // box for landscape mode
                Box(
                    modifier = Modifier
                        .weight(1f),
                ) {
                    IconToggleButton(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        checked = isSubscribed,
                        onCheckedChange = onToggleSubscription,
                    ) {
                        Icon(
                            imageVector = if (isSubscribed) Icons.Filled.Notifications else
                                Icons.Outlined.Notifications,
                            null
                        )
                    }
                }
            }
        }
    }

    Divider()
}
