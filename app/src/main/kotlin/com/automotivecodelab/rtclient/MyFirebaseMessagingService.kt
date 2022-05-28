package com.automotivecodelab.rtclient

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.automotivecodelab.rtclient.ui.MainActivity
import com.automotivecodelab.rtclient.ui.Screen
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        const val CHANNEL_ID = "channelId"
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val title = remoteMessage.data["title"]
        val feed = remoteMessage.data["feed"]!!
        val torrentId = remoteMessage.data["id"]!!
        val threadId = remoteMessage.data["threadId"]!!

        val deepLinkIntent = Intent(
            Intent.ACTION_VIEW,
            "${Screen.URI}/${Screen.FeedEntries.routeConstructor(
                threadId = threadId,
                title = feed,
                torrentIdToOpen = torrentId
            )}".toUri(),
            this,
            MainActivity::class.java
        )

        val uniqueId = Random(System.currentTimeMillis()).nextInt()

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE
        else 0
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, uniqueId, deepLinkIntent, flags
        )
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_notif)
            .setContentTitle(feed)
            .setContentText(title)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_EVENT)
            .setContentIntent(pendingIntent)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(title)
            )
            .setAutoCancel(true)

        NotificationManagerCompat.from(this).notify(uniqueId, builder.build())
    }
}
