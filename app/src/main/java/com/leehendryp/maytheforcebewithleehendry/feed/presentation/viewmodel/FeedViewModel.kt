package com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.switchMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leehendryp.maytheforcebewithleehendry.core.Resource
import com.leehendryp.maytheforcebewithleehendry.feed.domain.Character
import com.leehendryp.maytheforcebewithleehendry.feed.domain.FetchPeopleUseCase
import com.leehendryp.maytheforcebewithleehendry.feed.domain.Page
import com.leehendryp.maytheforcebewithleehendry.feed.domain.SaveFavoriteUseCase
import com.leehendryp.maytheforcebewithleehendry.feed.domain.SearchCharacterUseCase
import com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel.FeedAction.CloseFailureDialog
import com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel.FeedAction.Init
import com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel.FeedAction.LoadMore
import com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel.FeedAction.Search
import com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel.FeedState.ContentLoaded
import com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel.FeedState.EmptyList
import com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel.FeedState.Failure
import com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel.FeedState.Loading
import kotlinx.coroutines.launch
import javax.inject.Inject

class FeedViewModel @Inject constructor(
    private val fetchPageUseCase: FetchPeopleUseCase,
    private val searchCharacterUseCase: SearchCharacterUseCase
) : ViewModel() {

    private val action by lazy { MutableLiveData<FeedAction>(Init) }

    private val content by lazy { MutableLiveData<Content>() }

    val state: LiveData<FeedState> by lazy { switchMap(action) { process(it) } }

    fun dispatch(action: FeedAction) {
        this.action.value = action
    }

    private fun process(action: FeedAction): MediatorLiveData<FeedState> {
        return MediatorLiveData<FeedState>().apply {
            when (action) {
                Init -> fetch(1)
                LoadMore -> fetch(nextPage())
                is Search -> search(action.query)
                CloseFailureDialog -> reestablishContent()
            }
        }
    }

    private fun MediatorLiveData<FeedState>.search(query: String) {
        viewModelScope.launch {
            value = EmptyList
            addSourcesFrom(searchCharacterUseCase.execute(query))
        }
    }

    private fun MediatorLiveData<FeedState>.fetch(page: Int?) {
        page?.let {
            value = Loading
            viewModelScope.launch {
                addSourcesFrom(fetchPageUseCase.execute(it))
            }
        }
    }

    private fun MediatorLiveData<FeedState>.reestablishContent() {
        value = ContentLoaded(content.value ?: Content(setOf(), 1))
    }

    private fun MediatorLiveData<FeedState>.addSourcesFrom(resource: Resource<Page>) {
        with(resource) {
            addSource(data) { page ->
                var characters: Set<Character>? = null
                viewModelScope.launch { characters = page.characters.toSet() }

                value = ContentLoaded(Content(characters ?: setOf(), page.next))
                    .also { updateContent(it) }
            }

            addSource(error) { error ->
                value = Failure(error)
            }
        }
    }

    private fun nextPage(): Int? = content.value?.nextPage

    private fun updateContent(contentLoaded: ContentLoaded) {
        content.value = contentLoaded.content
    }
}