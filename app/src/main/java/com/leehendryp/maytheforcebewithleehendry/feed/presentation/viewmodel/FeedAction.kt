package com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel

import com.leehendryp.maytheforcebewithleehendry.feed.domain.Character

sealed class FeedAction {
    object Init : FeedAction()
    object LoadMore : FeedAction()
    data class Search(val query: String) : FeedAction()
    object CloseFailureDialog : FeedAction()
}
