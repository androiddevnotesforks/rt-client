package com.automotivecodelab.featuredetails.ui

import android.Manifest
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.automotivecodelab.featuredetails.domain.models.TorrentAction
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

    val context = LocalContext.current

    viewModel.magnetLinkEvent?.let { event ->
        if (!event.hasBeenHandled) {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, event.getContent())
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            context.startActivity(shareIntent)
        }
    }

    viewModel.openTorrentFileEvent?.let { event ->
        if (!event.hasBeenHandled) {
            val intent = Intent().apply {
                action = Intent.ACTION_VIEW
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                data = event.getContent()
            }
            context.startActivity(intent)
        }
    }

    viewModel.requestFilesystemPermissionEvent?.let { event ->
        if (!event.hasBeenHandled) {
            event.getContent()
            val permissionState = rememberPermissionState(
                permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            LaunchedEffect(key1 = true, block = {
                permissionState.launchPermissionRequest()
            })
        }
    }
    var isDialogShown by rememberSaveable {
        mutableStateOf(false)
    }
    if (isDialogShown) {
        ChangeDefaultActionDialog(
            onDismiss = { isDialogShown = false },
            viewModel = viewModel
        )
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
        // when system bottom sheet appears - e.g. sharing, opening torrent file - status bar
        // disappears. It makes all the content of details screen move upper and it looks not good
        // (when spacer have the .windowInsetsTopHeight(WindowInsets.statusBars) modifier).
        // Noticed on samsung One UI - maybe for other vendors this behavior is the same. So this is
        // a small workaround for that:
        val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        // rememberSaveable for handling rotation. In case of using remember {} there is zero value
        // in statusBarHeight after rotate device
        val statusBarHeightRemembered = rememberSaveable {
            statusBarHeight.value
        }
        LazyColumn(
            modifier = Modifier.padding(horizontal = DefaultPadding),
            verticalArrangement = Arrangement.spacedBy(DefaultPadding),
            content = {
                item {
                    Spacer(modifier = Modifier.height(statusBarHeightRemembered.dp))
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
                                        color = MaterialTheme.colors.primary
                                    )
                                ) {
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
                            horizontalArrangement = Arrangement.spacedBy(DefaultPadding),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val defaultAction by viewModel.defaultAction.collectAsState()
                            val shareLink = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, torrentDescription.url)
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, null)
                                context.startActivity(shareIntent)
                            }
                            when (defaultAction) {
                                TorrentAction.OPEN -> {
                                    OutlinedButtonWithLoadingIndicator(
                                        modifier = Modifier.weight(1f),
                                        isLoading = viewModel.isTorrentFileOpening,
                                        text = stringResource(id = R.string.open_torrent),
                                        onClick = {
                                            viewModel.openTorrentFile(torrentDescription.id)
                                        }
                                    )
                                }
                                TorrentAction.DOWNLOAD -> {
                                    OutlinedButtonWithLoadingIndicator(
                                        modifier = Modifier.weight(1f),
                                        isLoading = false,
                                        text = stringResource(id = R.string.download_torrent),
                                        onClick = {
                                            viewModel.downloadTorrentFile(
                                                torrentId = torrentDescription.id,
                                                title = torrentDescription.title
                                            )
                                        }
                                    )
                                }
                                TorrentAction.GET_MAGNET_LINK -> {
                                    OutlinedButtonWithLoadingIndicator(
                                        modifier = Modifier.weight(1f),
                                        isLoading = viewModel.isMagnetLinkLoading,
                                        text = stringResource(id = R.string.magnet_link),
                                        onClick = { viewModel.getMagnetLink(torrentDescription.id) }
                                    )
                                }
                                TorrentAction.SHARE_LINK -> {
                                    OutlinedButtonWithLoadingIndicator(
                                        modifier = Modifier.weight(1f),
                                        isLoading = false,
                                        text = stringResource(id = R.string.share_link),
                                        onClick = shareLink
                                    )
                                }
                            }
                            val isFavorite = viewModel.isFavorite.collectAsState()

                            IconToggleButton(
                                modifier = Modifier.size(26.dp),
                                checked = isFavorite.value,
                                onCheckedChange = { viewModel.toggleFavorite() }
                            ) {
                                Icon(
                                    painter = painterResource(
                                        id =
                                        if (isFavorite.value)
                                            com.automotivecodelab.coreui.R.drawable
                                                .ic_baseline_star_24
                                        else R.drawable.ic_baseline_star_border_24
                                    ),
                                    null
                                )
                            }

                            Box {
                                var isDropDownMenuExpanded by remember {
                                    mutableStateOf(false)
                                }
                                IconButton(
                                    modifier = Modifier.size(26.dp),
                                    onClick = { isDropDownMenuExpanded = true }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = null,
                                    )
                                }
                                DropdownMenu(
                                    expanded = isDropDownMenuExpanded,
                                    onDismissRequest = { isDropDownMenuExpanded = false }
                                ) {
                                    if (defaultAction != TorrentAction.OPEN) {
                                        DropdownMenuItem(onClick = {
                                            viewModel.openTorrentFile(torrentDescription.id)
                                            isDropDownMenuExpanded = false
                                        }) {
                                            Text(text = stringResource(id = R.string.open_torrent))
                                        }
                                    }
                                    if (defaultAction != TorrentAction.DOWNLOAD) {
                                        DropdownMenuItem(onClick = {
                                            viewModel.downloadTorrentFile(
                                                torrentId = torrentDescription.id,
                                                title = torrentDescription.title
                                            )
                                            isDropDownMenuExpanded = false
                                        }) {
                                            Text(text = stringResource(
                                                id = R.string.download_torrent
                                            ))
                                        }
                                    }
                                    if (defaultAction != TorrentAction.GET_MAGNET_LINK) {
                                        DropdownMenuItem(onClick = {
                                            viewModel.getMagnetLink(torrentDescription.id)
                                            isDropDownMenuExpanded = false
                                        }) {
                                            Text(text = stringResource(id = R.string.magnet_link))
                                        }
                                    }
                                    if (defaultAction != TorrentAction.SHARE_LINK) {
                                        DropdownMenuItem(onClick = {
                                            shareLink()
                                            isDropDownMenuExpanded = false
                                        }) {
                                            Text(text = stringResource(id = R.string.share_link))
                                        }
                                    }
                                    DropdownMenuItem(onClick = {
                                        isDialogShown = true
                                        isDropDownMenuExpanded = false
                                    }) {
                                        Text(text = stringResource(
                                            id = R.string.change_default_action
                                        ))
                                    }
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
                    item { Spacer(
                        modifier = Modifier
                            .windowInsetsBottomHeight(WindowInsets.navigationBars)
                    )
                    }
                }
            }
        )
    }
}

@Composable
fun OutlinedButtonWithLoadingIndicator(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    text: String,
    onClick: () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent // MaterialTheme.colors.background
        ),
        modifier = modifier
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChangeDefaultActionDialog(
    onDismiss: () -> Unit,
    viewModel: DetailsViewModel
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {  },
        containerColor = MaterialTheme.colors.surface,
        text = {
            Column {
                val defaultAction by viewModel.defaultAction.collectAsState()
                TorrentAction.values().forEach { action ->
                    val title = when (action) {
                        TorrentAction.OPEN -> R.string.open_torrent
                        TorrentAction.DOWNLOAD -> R.string.download_torrent
                        TorrentAction.GET_MAGNET_LINK -> R.string.magnet_link
                        TorrentAction.SHARE_LINK -> R.string.share_link
                    }
                    Surface(
                        onClick = { viewModel.changeDefaultAction(action) },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(DefaultPadding),
                            horizontalArrangement = Arrangement.spacedBy(DefaultPadding),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(modifier = Modifier.weight(1f), text = stringResource(id = title))
                            RadioButton(
                                selected = defaultAction == action,
                                onClick = null
                            )
                        }
                    }
                }
            }
        }
    )
}
