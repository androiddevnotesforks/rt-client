package com.automotivecodelab.featuredetailsbottomsheet.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.automotivecodelab.coreui.ui.theme.DefaultPadding
import com.automotivecodelab.featuredetailsbottomsheet.R
import com.automotivecodelab.featuredetailsbottomsheet.domain.models.SDUIFontWeight
import com.automotivecodelab.featuredetailsbottomsheet.domain.models.SDUITextModel
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.statusBarsHeight

@Composable
internal fun TorrentDetails(
    viewModel: BottomSheetDetailsViewModel,
    navigateToFeed: (title: String, threadId: String) -> Unit
) {
    if (viewModel.isDetailsLoading) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height((LocalConfiguration.current.screenHeightDp / 2).dp)
        ) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    } else {
        LazyColumn(
            modifier = Modifier.padding(horizontal = DefaultPadding),
            verticalArrangement = Arrangement.spacedBy(DefaultPadding),
            content = {
                item {
                    Spacer(modifier = Modifier.statusBarsHeight())
                }
                val torrentDescription = viewModel.torrentDescription
                if (torrentDescription != null) {
                    if (torrentDescription.formattedSize != null) {
                        item {
                            Text(
                                text =
                                buildAnnotatedString {
                                    append(stringResource(id = R.string.size))
                                    append(": ")
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append(torrentDescription.formattedSize)
                                    }
                                }
                            )
                        }
                    }
                    if (torrentDescription.timeAfterUpload != null) {
                        item {
                            Text(
                                text = buildAnnotatedString {
                                    append(stringResource(id = R.string.registered))
                                    append(": ")
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append(torrentDescription.timeAfterUpload)
                                    }
                                }
                            )
                        }
                    }
                    if (torrentDescription.downloads != null) {
                        item {
                            Text(
                                text =
                                buildAnnotatedString {
                                    append(stringResource(id = R.string.downloads))
                                    append(": ")
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append(torrentDescription.downloads)
                                    }
                                }
                            )
                        }
                    }
                    if (torrentDescription.seeds != null && torrentDescription.leeches != null) {
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(DefaultPadding)) {
                                Text(
                                    text =
                                    buildAnnotatedString {
                                        append(stringResource(id = R.string.seeds))
                                        append(": ")
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append(torrentDescription.seeds.toString())
                                        }
                                    },
                                    color = MaterialTheme.colors.secondary
                                )
                                Text(
                                    text =
                                    buildAnnotatedString {
                                        append(stringResource(id = R.string.leeches))
                                        append(": ")
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append(torrentDescription.leeches.toString())
                                        }
                                    },
                                    color = MaterialTheme.colors.secondaryVariant
                                )
                            }
                        }
                    }
                    if (torrentDescription.state != null) {
                        item {
                            Text(
                                text =
                                buildAnnotatedString {
                                    append(stringResource(id = R.string.state))
                                    append(": ")
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append(torrentDescription.state)
                                    }
                                }
                            )
                        }
                    }
                    if (torrentDescription.category != null &&
                        torrentDescription.threadId != null
                    ) {
                        item {
                            Text(
                                text = torrentDescription.category,
                                color = MaterialTheme.colors.primary,
                                modifier = Modifier.clickable {
                                    navigateToFeed(
                                        torrentDescription.category,
                                        torrentDescription.threadId
                                    )
                                }
                            )
                        }
                    }

                    item {
                        Text(
                            "${stringResource(id = R.string.author)}: " +
                                torrentDescription.author
                        )
                    }
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(DefaultPadding),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {

                            OutlinedButtonWithLoadingIndicator(
                                isLoading = viewModel.isMagnetLinkLoading,
                                text = stringResource(id = R.string.magnet_link),
                                onClick = { viewModel.getMagnetLink(torrentDescription.id) }
                            )

                            OutlinedButtonWithLoadingIndicator(
                                isLoading = false,
                                text = stringResource(id = R.string.torrent_file),
                                onClick = {
                                    viewModel.getTorrentFile(
                                        torrentDescription.id,
                                        torrentDescription.title
                                    )
                                }
                            )
                        }
                    }
                    item {
                        if (torrentDescription.SDUIData != null) {
                            torrentDescription.SDUIData.ToComposable()
                        } else if (torrentDescription.isWrongSDUIVersion) {
                            SDUIText(
                                SDUITextModel(
                                    stringResource(id = R.string.please_update_app),
                                    SDUIFontWeight.Regular
                                )
                            )
                        }
                    }
                    item {
                        val uriHandler = LocalUriHandler.current
                        OutlinedButton(
                            onClick = { uriHandler.openUri(torrentDescription.url) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.Transparent
                            )
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = R.drawable.ic_baseline_open_in_browser_24
                                ),
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(DefaultPadding))
                            Text(text = stringResource(id = R.string.open_in_browser))
                        }
                    }
                    item { Spacer(modifier = Modifier.navigationBarsHeight()) }
                }
            }
        )
    }
}

@Composable
fun OutlinedButtonWithLoadingIndicator(
    isLoading: Boolean,
    text: String,
    onClick: () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent // MaterialTheme.colors.background
        ),
        modifier = Modifier.width(150.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 3.dp
            )
        } else {
            Text(text = text)
        }
    }
}
