package com.leehendryp.maytheforcebewithleehendry.feed.presentation.view

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class EndlessOnScrollListener(
    private val onLoadMore: () -> Unit
) : RecyclerView.OnScrollListener() {

    private var previousTotal = 1
    private var isLoading = true

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

        if (recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
            return
        }

        val totalItemCount = recyclerView.layoutManager?.itemCount
        val lastVisibleItem =
            (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()

        totalItemCount?.let {
            if (isLoading) {
                if (it > previousTotal) {
                    isLoading = false
                    previousTotal = totalItemCount
                }
            }
        }

        val visibleThreshold = 1

        totalItemCount?.let {
            if (!isLoading && it <= lastVisibleItem + visibleThreshold) {
                onLoadMore()
                isLoading = true
            }
        }
    }
}