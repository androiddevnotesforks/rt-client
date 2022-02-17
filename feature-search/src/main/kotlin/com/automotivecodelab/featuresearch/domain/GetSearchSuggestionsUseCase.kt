package com.automotivecodelab.featuresearch.domain

import javax.inject.Inject

class GetSearchSuggestionsUseCase @Inject constructor(
    private val torrentSearchResultRepository: TorrentSearchResultRepository
) {
    suspend operator fun invoke(query: String): Result<List<String>> {
        return torrentSearchResultRepository.getSearchSuggestions(query)
    }
}
