package com.automotivecodelab.featuredetails.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import coil.size.OriginalSize
import coil.size.Size
import com.automotivecodelab.coreui.ui.theme.DefaultCornerRadius
import com.automotivecodelab.coreui.ui.theme.DefaultPadding
import com.automotivecodelab.coreui.ui.theme.Gray
import com.automotivecodelab.coreui.ui.theme.LightGray
import com.automotivecodelab.featuredetails.domain.models.*
import com.automotivecodelab.featuredetails.domain.models.SDUIColumnModel
import com.automotivecodelab.featuredetails.domain.models.SDUIDividerModel
import com.automotivecodelab.featuredetails.domain.models.SDUIFontWeight
import com.automotivecodelab.featuredetails.domain.models.SDUIHiddenContentModel
import com.automotivecodelab.featuredetails.domain.models.SDUIImageModel
import com.automotivecodelab.featuredetails.domain.models.SDUILinkModel
import com.automotivecodelab.featuredetails.domain.models.SDUIRowModel
import com.automotivecodelab.featuredetails.domain.models.SDUITextModel
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer

@Composable
fun SDUIComponent.ToComposable() {
    return when (this) {
        is SDUIImageModel -> SDUIImage(image = this)
        is SDUIColumnModel -> SDUIColumn(column = this)
        is SDUIHiddenContentModel -> SDUIHiddenContent(hiddenContent = this)
        is SDUILinkModel -> SDUILink(link = this)
        is SDUIRowModel -> SDUIRow(row = this)
        is SDUITextModel -> SDUIText(text = this)
        is SDUIDividerModel -> SDUIDivider()
    }
}

@Composable
fun SDUIImage(image: SDUIImageModel) {
    Box(modifier = Modifier.fillMaxWidth()) {
        val modifier = if (image.width != null && image.height != null) {
            Modifier
                .size(image.width.dp, image.height.dp)
                .align(Alignment.Center)
        } else {
            Modifier.fillMaxWidth()
        }

        var isLoading by remember {
            mutableStateOf(true)
        }
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(image.url)
                .size(Size.ORIGINAL)
                .crossfade(true)
                .listener(
                    onStart = { isLoading = true },
                    onCancel = { isLoading = false },
                    onError = { _, _ -> isLoading = false },
                    onSuccess = { _, _ -> isLoading = false }
                )
                .build(),
            contentDescription = null,
            modifier = modifier.placeholder(
                visible = isLoading,
                shape = RoundedCornerShape(DefaultCornerRadius),
                color = Gray,
                highlight = PlaceholderHighlight.shimmer(
                    highlightColor = LightGray
                )
            ),
            contentScale = ContentScale.Crop

        )
    }
}

@Composable
fun SDUIText(text: SDUITextModel) {
    val fontWeight = when (text.fontWeight) {
        SDUIFontWeight.Bold -> FontWeight.Bold
        SDUIFontWeight.Regular -> FontWeight.Normal
        SDUIFontWeight.Thin -> FontWeight.Thin
    }
    Text(text = text.text, fontWeight = fontWeight, textAlign = TextAlign.Start)
}

@Composable
fun SDUIColumn(column: SDUIColumnModel) {
    Column(verticalArrangement = Arrangement.spacedBy(DefaultPadding)) {
        column.children.forEach {
            it.ToComposable()
        }
    }
}

@Composable
fun SDUIRow(row: SDUIRowModel) {
    Row(
        modifier = Modifier.padding(DefaultPadding),
        horizontalArrangement = Arrangement.spacedBy(DefaultPadding)
    ) {
        row.children.forEach {
            it.ToComposable()
        }
    }
}

@Composable
fun SDUIHiddenContent(hiddenContent: SDUIHiddenContentModel) {
    var isHidden by remember {
        mutableStateOf(true)
    }
    Surface(
        modifier = Modifier
            .animateContentSize()
            .fillMaxWidth(),
        color = MaterialTheme.colors.background
    ) {
        Column {
            val rotation = if (isHidden) 0f else 180f
            val rotationAnim by animateFloatAsState(targetValue = rotation)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Start)
                    .clickable { isHidden = !isHidden },
                horizontalArrangement = Arrangement.spacedBy(DefaultPadding)
            ) {
                Text(text = hiddenContent.title)
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Desc",
                    modifier = Modifier.rotate(rotationAnim)
                )
            }

            if (!isHidden) {
                Spacer(modifier = Modifier.height(DefaultPadding))
                SDUIColumn(column = SDUIColumnModel(hiddenContent.children))
            }
        }
    }
}

@Composable
fun SDUILink(link: SDUILinkModel) {
    val uriHandler = LocalUriHandler.current
    Text(
        text = link.text ?: link.url,
        color = MaterialTheme.colors.primary,
        modifier = Modifier.clickable { uriHandler.openUri(link.url) }
    )
}

@Composable
fun SDUIDivider() {
    Divider()
}
