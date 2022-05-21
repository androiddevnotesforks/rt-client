package com.automotivecodelab.rtclient.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.core.view.WindowCompat
import com.automotivecodelab.coreui.ui.Event
import com.automotivecodelab.rtclient.MyFirebaseMessagingService
import com.automotivecodelab.rtclient.R
import com.google.accompanist.insets.ProvideWindowInsets
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import timber.log.Timber

class MainActivity : ComponentActivity() {

    private var deepLinkEvent by mutableStateOf<Event<Intent>?>(null)

    @OptIn(
        ExperimentalAnimationApi::class, ExperimentalMaterialApi::class,
        ExperimentalComposeUiApi::class, FlowPreview::class, ExperimentalCoroutinesApi::class,
        DelicateCoroutinesApi::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_RTClient)
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            ProvideWindowInsets {
                HostScreen(deepLinkEvent)
            }
        }
        // safe to call repeatedly as docs said
        createNotificationChannel()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null && intent.data != null) {
            deepLinkEvent = Event(intent)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notification_channel_name)
            val descriptionText = getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                MyFirebaseMessagingService.CHANNEL_ID,
                name,
                importance
            )
                .apply { description = descriptionText }
            val notificationManager: NotificationManager = getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
