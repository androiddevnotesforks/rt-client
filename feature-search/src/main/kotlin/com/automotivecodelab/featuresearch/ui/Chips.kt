package com.automotivecodelab.featuresearch.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.automotivecodelab.coreui.ui.theme.DefaultPadding
import com.automotivecodelab.featuresearch.R
import com.automotivecodelab.featuresearch.domain.models.Order
import com.automotivecodelab.featuresearch.domain.models.Sort

sealed class ChipState {
    object Inactive : ChipState()
    class Active(val order: Order) : ChipState()
}

@Composable
fun Chip(
    text: String,
    state: ChipState,
    onClick: () -> Unit
) {
    val surfaceColor = if (state is ChipState.Active)
        MaterialTheme.colors.primary.copy(alpha = 0.2f)
    else MaterialTheme.colors.surface
    val contentColor = MaterialTheme.colors.onSurface.copy(alpha = TextFieldDefaults.IconOpacity)
    Surface(
        color = surfaceColor,
        border = if (state is ChipState.Active) {
            null
        } else {
            BorderStroke(
                width = 1.dp,
                color = contentColor
            )
        },
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .padding(DefaultPadding / 2)
            .height(32.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .animateContentSize()
                .clickable(onClick = onClick)
        ) {
            if (state is ChipState.Active) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            } else {
                Spacer(modifier = Modifier.width(16.dp))
            }
            Text(
                text = text,
                color = contentColor
            )
            if (state is ChipState.Active) {
                val rotation = if (state.order == Order.Desc) 0f else 180f
                val rotationAnim by animateFloatAsState(targetValue = rotation)
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .rotate(rotationAnim),
                    tint = contentColor
                )
            } else {
                Spacer(modifier = Modifier.width(16.dp))
            }
        }
    }
}

@Composable
fun ChipRow(
    currentSort: Sort,
    currentOrder: Order,
    onSortChange: (Sort) -> Unit,
    onOrderChange: () -> Unit,
    lazyListState: LazyListState
) {
    LazyRow(
        contentPadding = PaddingValues(DefaultPadding / 2),
        state = lazyListState
    ) {
        items(items = Sort.values()) { sort ->
            val chipState = if (currentSort == sort) {
                ChipState.Active(currentOrder)
            } else {
                ChipState.Inactive
            }
            val text = when (sort) {
                Sort.Registered -> R.string.registered
                Sort.Title -> R.string.title
                Sort.Downloads -> R.string.downloads
                Sort.Size -> R.string.size
                Sort.LastMessage -> R.string.last_message
                Sort.Seeds -> R.string.seeds
                Sort.Leeches -> R.string.leeches
            }.let { stringResource(id = it) }
            Chip(
                text = text,
                state = chipState
            ) {
                when (chipState) {
                    is ChipState.Inactive -> onSortChange(sort)
                    is ChipState.Active -> onOrderChange()
                }
            }
        }
    }
}
