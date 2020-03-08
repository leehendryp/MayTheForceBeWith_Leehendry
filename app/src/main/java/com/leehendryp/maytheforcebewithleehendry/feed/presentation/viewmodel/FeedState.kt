package com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import com.leehendryp.maytheforcebewithleehendry.feed.domain.Character
import com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel.FeedState.Error
import com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel.FeedState.Loading
import com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel.FeedState.Success
import com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel.FeedState.Default

sealed class FeedState {
    object Default : FeedState()
    object Loading : FeedState()
    data class Success(val data: List<Character>) : FeedState()
    data class Error(val error: Throwable) : FeedState()
}

fun MutableLiveData<FeedState>.toDefault() {
    value = Default
}

fun MutableLiveData<FeedState>.toLoading() = postValue(Loading)

fun MutableLiveData<FeedState>.toSuccess(data: List<Character>) = postValue(Success(data))

fun MutableLiveData<FeedState>.toError(error: Throwable) = postValue(Error(error))