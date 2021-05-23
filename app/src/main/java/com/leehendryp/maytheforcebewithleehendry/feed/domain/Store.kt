package com.leehendryp.maytheforcebewithleehendry.feed.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leehendryp.maytheforcebewithleehendry.core.Resource
import com.leehendryp.maytheforcebewithleehendry.core.Resource.*
import com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel.Content
import com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel.FeedAction
import com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel.FeedState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.lang.IllegalStateException
import java.util.concurrent.LinkedBlockingQueue
import javax.inject.Inject

abstract class Store @Inject constructor(
    private val fetchReducer: FetchReducer
) {
    private val coroutineScope = CoroutineScope(IO)

    init {
        dispatch(action = FeedAction.Init)
    }

    private var actions = sequenceOf<FeedAction>()
    var states = sequenceOf<FeedState>(FeedState.EmptyList)
        protected set

    fun dispatch(action: FeedAction) {
        actions + action

        coroutineScope.launch {
            handle(action)
        }
    }

    private fun handle(action: FeedAction) {
        coroutineScope.launch {
            val newState = reduce(action, states.last())
            dispatch(newState)
        }
    }

    private fun dispatch(state: FeedState) {
        updateState(state)
    }

    private suspend fun reduce(action: FeedAction, state: FeedState): FeedState {
        val newState = when (action) {
            FeedAction.Init -> fetchReducer.reduce(action, state)
            FeedAction.LoadMore -> fetchReducer.reduce(nextPage(), state)
            is FeedAction.Search -> search(action.query)
            FeedAction.CloseFailureDialog -> reestablishContent()
        }
        updateState(newState)
        return newState
    }


    private fun updateState(newState: FeedState) {
        states + newState
    }

    class FetchReducer @Inject constructor(private val fetchPageUseCase: FetchPeopleUseCase) {
        suspend fun reduce(action: FeedAction, state: FeedState): FeedState {
            return when (action) {
                FeedAction.Init -> fetch(1, state)
                FeedAction.LoadMore -> FeedState.EmptyList
                else -> throw Exception()
            }
        }

        private suspend fun fetch(page: Int, state: FeedState): FeedState {
            return when (val resource = fetchPageUseCase.execute(1)) {
                is Success<Page, Nothing> -> FeedState.EmptyList
                is Error<Nothing, Throwable> -> FeedState.EmptyList
                else -> throw Exception()
            }
        }
    }
}

class FeedViewModelRedux @Inject constructor(
    private val fetchPageUseCase: FetchPeopleUseCase,
    private val searchCharacterUseCase: SearchCharacterUseCase
) : ViewModel() {

    private val action by lazy { MutableLiveData<FeedAction>(FeedAction.Init) }

    private val content by lazy { MutableLiveData<Content>() }

    val state: LiveData<FeedState> by lazy { Transformations.switchMap(action) { process(it) } }

    fun dispatch(action: FeedAction) {
        this.action.value = action
    }

    private fun process(action: FeedAction): MediatorLiveData<FeedState> {
        return MediatorLiveData<FeedState>().apply {
            when (action) {
                FeedAction.Init -> fetch(1)
                FeedAction.LoadMore -> fetch(nextPage())
                is FeedAction.Search -> search(action.query)
                FeedAction.CloseFailureDialog -> reestablishContent()
            }
        }
    }

    private fun MediatorLiveData<FeedState>.search(query: String) {
        viewModelScope.launch {
            value = FeedState.EmptyList
            addSourcesFrom(searchCharacterUseCase.execute(query))
        }
    }

    private fun MediatorLiveData<FeedState>.fetch(page: Int?) {
        page?.let {
            value = FeedState.Loading
            viewModelScope.launch {
                addSourcesFrom(fetchPageUseCase.execute(it))
            }
        }
    }

    private fun MediatorLiveData<FeedState>.reestablishContent() {
        value = FeedState.ContentLoaded(content.value ?: Content(setOf(), 1))
    }

    private fun MediatorLiveData<FeedState>.addSourcesFrom(resource: Resource<Page>) {
        with(resource) {
            addSource(data) { page ->
                var characters: Set<Character>? = null
                viewModelScope.launch { characters = page.characters.toSet() }

                value = FeedState.ContentLoaded(Content(characters ?: setOf(), page.next))
                    .also { updateContent(it) }
            }

            addSource(error) { error ->
                value = FeedState.Failure(error)
            }
        }
    }

    private fun nextPage(): Int? = content.value?.nextPage

    private fun updateContent(contentLoaded: FeedState.ContentLoaded) {
        content.value = contentLoaded.content
    }
}