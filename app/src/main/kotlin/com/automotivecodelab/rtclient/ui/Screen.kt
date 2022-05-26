package com.automotivecodelab.rtclient.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.automotivecodelab.rtclient.R
import timber.log.Timber

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

    object Favorites : Screen(
        routeId = "favorites",
        icon = com.automotivecodelab.coreui.R.drawable.ic_baseline_star_24,
        label = R.string.favorites
    )

    object Details : Screen(
        routeId = "details?torrent_id={torrent_id}&category={category}&author={author}&" +
                "title={title}&url={url}",
        icon = null,
        label = null
    ) {
        fun routeConstructor(
            torrentId: String,
            category: String,
            author: String,
            title: String,
            url: String,
        ) : String {
            // path parsing by navigation component goes wrong when there is "#" symbol in path
            val _category = category.replace('#', ' ')
            val _author = author.replace('#', ' ')
            val _title = title.replace('#', ' ')
            return "details?$TORRENT_ID=$torrentId&$CATEGORY=$_category&$AUTHOR=$_author&" +
                    "$TITLE=$_title&$URL=$url"
        }
        const val TORRENT_ID = "torrent_id"
        const val CATEGORY = "category"
        const val AUTHOR = "author"
        const val TITLE = "title"
        const val URL = "url"
    }
}
