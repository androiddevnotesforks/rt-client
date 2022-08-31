package com.automotivecodelab.coreui.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.automotivecodelab.corenetwork.data.NoInternetConnectionException
import com.automotivecodelab.coreui.BuildConfig
import com.automotivecodelab.coreui.R
import com.google.accompanist.insets.navigationBarsPadding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun Event<Throwable>.ShowErrorSnackbar(scaffoldState: ScaffoldState) {
    if (!hasBeenHandled) {
        val message = getUserFriendlyErrorMessage(t = this.getContent())
        LaunchedEffect(key1 = message) {
            scaffoldState.snackbarHostState.run {
                currentSnackbarData?.dismiss()
                showSnackbar(message)
            }
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun Event<Throwable>.ShowErrorSnackbar(
    scaffoldState: ScaffoldState,
    coroutineScope: CoroutineScope
) {
    if (!hasBeenHandled) {
        val message = getUserFriendlyErrorMessage(t = this.getContent())
        coroutineScope.launch {
            scaffoldState.snackbarHostState.run {
                currentSnackbarData?.dismiss()
                showSnackbar(message)
            }
        }
    }
}

@Composable
private fun getUserFriendlyErrorMessage(t: Throwable): String {
    val noInternetConnectionMessage = stringResource(id = R.string.no_connection)
    val defaultErrorMessage = stringResource(id = R.string.error_message)

    return when {
        BuildConfig.DEBUG -> t.message.toString()
        t is NoInternetConnectionException -> noInternetConnectionMessage
        else -> defaultErrorMessage
    }
}

@Composable
fun SnackbarWithInsets(
    snackbarHostState: SnackbarHostState
) {
    SnackbarHost(
        hostState = snackbarHostState,
        snackbar = { snackbarData ->
            Snackbar(
                modifier =
                Modifier
                    .padding(16.dp)
                    .navigationBarsPadding()
            ) {
                Text(text = snackbarData.message, style = MaterialTheme.typography.body2)
            }
        }
    )
}
