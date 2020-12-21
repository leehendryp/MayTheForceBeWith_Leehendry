package com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.switchMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.leehendryp.maytheforcebewithleehendry.feed.data.remote.Resource
import com.leehendryp.maytheforcebewithleehendry.feed.domain.FetchPeopleUseCase
import com.leehendryp.maytheforcebewithleehendry.feed.domain.Page
import com.leehendryp.maytheforcebewithleehendry.feed.domain.SaveFavoriteUseCase
import com.leehendryp.maytheforcebewithleehendry.feed.domain.SearchCharacterUseCase
import com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel.Action.CloseFailureDialog
import com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel.Action.Fetch
import com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel.Action.ScrollDown
import com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel.UIState.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class FeedViewModel @Inject constructor(
    private val fetchPageUseCase: FetchPeopleUseCase,
    private val searchCharacterUseCase: SearchCharacterUseCase,
    private val saveFavoriteUseCase: SaveFavoriteUseCase
) : ViewModel() {

    private val action by lazy { MutableLiveData<Action>(Fetch(1)) }
    fun dispatch(action: Action) {
        this.action.value = action
    }

    val state: LiveData<UIState> by lazy { switchMap(action) { process(it) } }
    private fun process(action: Action) = MediatorLiveData<UIState>().apply {
        Loading
        when (action) {
            is Fetch -> fetch(action.page)
            is ScrollDown -> Idle
            is CloseFailureDialog -> Idle
            else -> Idle
        }
    }

    private fun MediatorLiveData<UIState>.fetch(page: Int) {
        viewModelScope.launch {
            addSourcesFrom(fetchPageUseCase.execute(page))
        }
    }

    private fun MediatorLiveData<UIState>.fetchNext() {
        // E QUANDO HOUVER ERRO NO MEIO?
        // COM FLOW, DÁ PRA RECUPERAR A ÚLTIMA EMISSÃO DE SUCESSO E USAR O NÚMERO DE NOVO
        val nextPage = (state.value as PageLoaded).data.next

        nextPage?.let {
            viewModelScope.launch {
                addSourcesFrom(fetchPageUseCase.execute(it))
            }
        }
    }

    private fun MediatorLiveData<UIState>.addSourcesFrom(resource: Resource<Page>) {
        with(resource) {
            addSource(data) { page -> emit(PageLoaded(page)) }
            addSource(error) { error -> emit(Failure(error)) }
        }
    }

    private fun <T> emit(data: T): LiveData<T> = liveData { emit(data) }
}

sealed class UIState {
    object Loading : UIState()
    data class PageLoaded(val data: Page) : UIState()
    data class Failure(val error: Throwable) : UIState()
    object Idle : UIState()
}

sealed class Action {
    data class Fetch(val page: Int) : Action()
    data class Search(val query: String) : Action()
    object CloseFailureDialog : Action()
    object ScrollDown : Action()
}