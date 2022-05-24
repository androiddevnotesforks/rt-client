package com.automotivecodelab.featuredetails.ui

import android.Manifest
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.automotivecodelab.coreui.ui.ShowErrorSnackbar
import com.automotivecodelab.coreui.ui.injectViewModel
import com.automotivecodelab.coreui.ui.theme.DefaultPadding
import com.automotivecodelab.featuredetails.R
import com.automotivecodelab.featuredetails.di.DaggerDetailsComponent
import com.automotivecodelab.featuredetails.di.DetailsComponentDeps
import com.automotivecodelab.featuredetails.domain.models.SDUIFontWeight
import com.automotivecodelab.featuredetails.domain.models.SDUITextModel
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.statusBarsHeight
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TorrentDetails(
    torrentId: String,
    category: String,
    author: String,
    title: String,
    url: String,
    navigateToFeed: (title: String, threadId: String) -> Unit,
    detailsDeps: DetailsComponentDeps,
    scaffoldState: ScaffoldState,
    coroutineScope: CoroutineScope,
) {
    val component = remember {
        DaggerDetailsComponent.builder()
            .detailsComponentDeps(detailsDeps)
            .build()
    }
    val viewModel = injectViewModel {
        component.detailsViewModelFactory().create(
            torrentId = torrentId,
            category = category,
            author = author,
            title = title,
            url = url
        )
    }

    viewModel.error?.ShowErrorSnackbar(scaffoldState, coroutineScope)

    viewModel.magnetLinkEvent?.let { event ->
        if (!event.hasBeenHandled) {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, event.getContent())
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            LocalContext.current.startActivity(shareIntent)
        }
    }

    viewModel.requestFilesystemPermissionEvent?.let { event ->
        if (!event.hasBeenHandled) {
            event.getContent()
            val permissionState = rememberPermissionState(
                permission = Manifest.permission.WRITE_EXTERNAL_STORAGE)
            LaunchedEffect(key1 = true, block = {
                permissionState.launchPermissionRequest()
            })
        }
    }

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
                    if (torrentDescription.threadId != null) {
                        item {
                            val str = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        textDecoration = TextDecoration.Underline,
                                        color = MaterialTheme.colors.primary)) {
                                    append(torrentDescription.category)
                                }
                            }
                            Text(
                                text = str,
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
                            val isFavorite = viewModel.isFavorite?.collectAsState()
                            if (isFavorite != null) {
                                IconToggleButton(
                                    checked = isFavorite.value,
                                    onCheckedChange = { viewModel.toggleFavorite() }
                                ) {
                                    Icon(
                                        painter = painterResource(id =
                                            if (isFavorite.value) R.drawable.ic_baseline_star_24
                                            else R.drawable.ic_baseline_star_border_24
                                        ),
                                        null
                                    )

                                }
                            }
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
