package com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel

sealed class FeedAction {
    object Init : FeedAction()
    object LoadMore : FeedAction()
    object CloseFailureDialog : FeedAction()
}
