package com.automotivecodelab.featuredetailsbottomsheet.ui

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.automotivecodelab.coreui.ui.theme.DefaultCornerRadius
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun BottomSheetDetailsLayout(
    viewModel: BottomSheetDetailsViewModel,
    navigateToFeed: (title: String, threadId: String) -> Unit,
    isDarkMode: Boolean,
    screenContent: @Composable () -> Unit
) {

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

    val coroutineScope = rememberCoroutineScope()

    BackHandler(enabled = viewModel.modalBottomSheetState.isVisible) {
        coroutineScope.launch { viewModel.modalBottomSheetState.hide() }
    }

    viewModel.openBottomSheetEvent?.let { event ->
        if (!event.hasBeenHandled) {
            event.getContent() // mark as handled
            coroutineScope.launch { viewModel.modalBottomSheetState.show() }
        }
    }

    ModalBottomSheetLayout(
        sheetContent = {
            TorrentDetails(
                viewModel = viewModel,
                navigateToFeed = navigateToFeed
            )
        },
        sheetState = viewModel.modalBottomSheetState,
        sheetShape = RoundedCornerShape(
            topStart = DefaultCornerRadius,
            topEnd = DefaultCornerRadius
        ),
        sheetBackgroundColor = MaterialTheme.colors.background,
        sheetElevation = if (isDarkMode) 0.dp else ModalBottomSheetDefaults.Elevation
    ) {
        screenContent()
    }
}
