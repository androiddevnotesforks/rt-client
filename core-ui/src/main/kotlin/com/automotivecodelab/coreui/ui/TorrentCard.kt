package com.automotivecodelab.coreui.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.automotivecodelab.coreui.ui.theme.DefaultPadding
import java.util.*

@Composable
fun TorrentCard(
    title: String,
    updated: Date?,
    author: String,
    category: String?,
    formattedSize: String?,
    seeds: Int?,
    leeches: Int?,
    onClick: () -> Unit
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = MaterialTheme.colors.background
    ) {
        Column {
            Column(
                verticalArrangement = Arrangement.spacedBy(DefaultPadding),
                modifier = Modifier.padding(DefaultPadding)
            ) {
                Text(category ?: author, style = MaterialTheme.typography.caption)
                Text(
                    text = title,
                    color = MaterialTheme.colors.primary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    formattedSize?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.body2
                        )
                    }
                    seeds?.let {
                        Text(
                            text = "S: $it",
                            color = MaterialTheme.colors.secondary,
                            style = MaterialTheme.typography.body2
                        )
                    }
                    leeches?.let {
                        Text(
                            text = "L: $it",
                            color = MaterialTheme.colors.secondaryVariant,
                            style = MaterialTheme.typography.body2
                        )
                    }
                    updated?.let {
                        Text(
                            text = it.formatDate(),
                            style = MaterialTheme.typography.body2
                        )
                    }
                }
            }
        }
    }
    //Divider()
}
