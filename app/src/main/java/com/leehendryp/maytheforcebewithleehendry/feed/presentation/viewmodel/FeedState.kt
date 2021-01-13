package com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel

import com.leehendryp.maytheforcebewithleehendry.feed.domain.Character

sealed class FeedState {
    object Loading : FeedState()
    data class ContentLoaded(val content: Content) : FeedState()
    data class Failure(val error: Throwable) : FeedState()
    object EmptyList : FeedState()
}

data class Content(
    val characters: Set<Character>,
    val nextPage: Int? = null
)