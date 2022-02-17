package com.automotivecodelab.featurerssfeeds.ui.rssfeedsscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.automotivecodelab.coreui.ui.theme.DefaultPadding
import com.automotivecodelab.featurerssfeeds.R

@Composable
fun AddRssChannelDialog(
    state: AddRssChannelDialogState,
    onStateChange: (AddRssChannelDialogState) -> Unit,
    onConfirm: () -> Unit,
    url: String,
    onUrlChange: (String) -> Unit
) {

    androidx.compose.material3.AlertDialog(
        onDismissRequest = { onStateChange(AddRssChannelDialogState.HIDDEN) },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = state != AddRssChannelDialogState.LOADING
            ) {
                Text(
                    text = stringResource(id = R.string.add),
                )
            }
        },
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_rss_feed_24),
                contentDescription = null
            )
        },
        title = { Text(text = stringResource(id = R.string.thread_url)) },
        containerColor = MaterialTheme.colors.surface,
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(DefaultPadding)
            ) {
                Text(
                    text = stringResource(id = R.string.add_thread_hint),
                    color = MaterialTheme.colors.onSurface.copy(
                        alpha = TextFieldDefaults
                            .IconOpacity
                    ),
                    style = MaterialTheme.typography.body2
                )
                OutlinedTextField(
                    value = url,
                    onValueChange = onUrlChange,
                    modifier = Modifier
                        .fillMaxWidth(),
                    placeholder = { Text("http://rutracker.org/forum/viewforum.php?f=") },
                    maxLines = 2,
                    isError = state == AddRssChannelDialogState.INPUT_ERROR,
                    enabled = state != AddRssChannelDialogState.LOADING
                )
            }
        }
    )
}
