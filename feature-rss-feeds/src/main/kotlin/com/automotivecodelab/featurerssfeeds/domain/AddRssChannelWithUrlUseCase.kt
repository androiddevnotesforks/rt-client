package com.automotivecodelab.featurerssfeeds.domain

import android.net.Uri
import java.util.*
import javax.inject.Inject

class AddRssChannelWithUrlUseCase @Inject constructor(
    private val rssChannelRepository: RssChannelRepository
) {
    suspend operator fun invoke(threadUrl: String): Result<Unit> {
        // kind of clean arch violation - android framework class Uri is here
        val threadId = Uri.parse(threadUrl).getQueryParameter("f")
        if (threadId == null || !threadUrl.contains("forum/viewforum.php")) {
            return Result.failure(InputMismatchException())
        }
        return rssChannelRepository.addRssChannel(threadId)
    }
}
