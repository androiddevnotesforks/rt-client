package com.automotivecodelab.coreui.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.automotivecodelab.coreui.R
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
    isFavorite: Boolean?,
    onClick: () -> Unit
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = MaterialTheme.colors.background
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(DefaultPadding),
            modifier = Modifier.padding(DefaultPadding)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(category ?: author, style = MaterialTheme.typography.caption)
                if (isFavorite == true) {
                    Icon(
                        modifier = Modifier.size(18.dp),
                        painter = painterResource(R.drawable.ic_baseline_star_24),
                        contentDescription = null
                    )
                }
            }
            Text(
                text = title,
                color = MaterialTheme.colors.primary
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (formattedSize != null) {
                    Text(
                        text = formattedSize,
                        style = MaterialTheme.typography.body2
                    )
                }
                if (seeds != null) {
                    Text(
                        text = "S: $seeds",
                        color = MaterialTheme.colors.secondary,
                        style = MaterialTheme.typography.body2
                    )
                }
                if (leeches != null) {
                    Text(
                        text = "L: $leeches",
                        color = MaterialTheme.colors.secondaryVariant,
                        style = MaterialTheme.typography.body2
                    )
                }
                if (updated != null) {
                    Text(
                        text = updated.formatDate(),
                        style = MaterialTheme.typography.body2
                    )
                }
            }
        }

    }
}

@Preview
@Composable
fun TorrentCardPreview() {
    TorrentCard(
        title = "Torrent name",
        updated = Date(),
        author = "author",
        category = "category",
        formattedSize = "5GB",
        seeds = 6,
        leeches = 8,
        isFavorite = true
    ) {

    }
}
