package com.automotivecodelab.featuresearch.ui

sealed class TrendsLoadingState {
    object Loading : TrendsLoadingState()
    object Error : TrendsLoadingState()
    class Success(val data: List<String>) : TrendsLoadingState()
}
