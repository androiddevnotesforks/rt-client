package com.automotivecodelab.rtclient.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.automotivecodelab.rtclient.R

sealed class Screen(
    val routeId: String,
    @DrawableRes val icon: Int?,
    @StringRes val label: Int?
) {
    companion object {
        const val URI = "https://www.example.com"
    }

    object Search : Screen(
        routeId = "search",
        icon = R.drawable.ic_baseline_search_24,
        label = R.string.search
    )

    object Feeds : Screen(
        routeId = "feeds",
        icon = R.drawable.ic_baseline_rss_feed_24,
        label = R.string.threads
    )

    object FeedEntries : Screen(
        routeId = "feed_entries?thread_id={thread_id}&title={title}&torrent_id={torrent_id}",
        icon = null,
        label = null
    ) {
        fun routeConstructor(threadId: String, title: String, torrentIdToOpen: String?): String {
            val torrentIdParam = if (torrentIdToOpen == null) {
                ""
            } else {
                "&$TORRENT_ID=$torrentIdToOpen"
            }
            return "feed_entries?$THREAD_ID=$threadId&$TITLE=$title".plus(torrentIdParam)
        }

        const val THREAD_ID = "thread_id"
        const val TITLE = "title"
        const val TORRENT_ID = "torrent_id"
    }
}
