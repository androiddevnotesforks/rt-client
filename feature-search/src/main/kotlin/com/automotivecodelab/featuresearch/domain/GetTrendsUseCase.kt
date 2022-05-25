package com.automotivecodelab.featuresearch.domain

import javax.inject.Inject

class GetTrendsUseCase @Inject constructor(
    private val torrentSearchResultRepository: TorrentSearchResultRepository
) {
    suspend operator fun invoke(): Result<List<String>> {
        return torrentSearchResultRepository.getTrends()
    }
}