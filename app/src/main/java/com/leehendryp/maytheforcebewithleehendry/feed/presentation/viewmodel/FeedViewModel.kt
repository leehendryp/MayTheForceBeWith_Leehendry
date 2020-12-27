package com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leehendryp.maytheforcebewithleehendry.feed.domain.FetchPeopleUseCase
import com.leehendryp.maytheforcebewithleehendry.feed.domain.Page
import com.leehendryp.maytheforcebewithleehendry.feed.domain.SaveFavoriteUseCase
import com.leehendryp.maytheforcebewithleehendry.feed.domain.SearchCharacterUseCase
import com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel.Action.CloseFailureDialog
import com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel.Action.FetchNext
import com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel.Action.Init
import com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel.Action.ScrollDown
import com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel.UIState.Failure
import com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel.UIState.Idle
import com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel.UIState.Loading
import com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel.UIState.PageLoaded
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

private const val FIRST_PAGE = 1

class FeedViewModel @Inject constructor(
    private val fetchPageUseCase: FetchPeopleUseCase,
    private val searchCharacterUseCase: SearchCharacterUseCase,
    private val saveFavoriteUseCase: SaveFavoriteUseCase
) : ViewModel() {

    private val action by lazy { MutableLiveData<Action>(Init) }

    fun dispatch(action: Action) {
        this.action.value = action
    }

    val state: Flow<UIState> by lazy {
        flow<UIState> {
            Loading
            when (action.value) {
                Init -> fetch(FIRST_PAGE)
                FetchNext -> fetch(nextPage())
                is ScrollDown -> Idle
                is CloseFailureDialog -> Idle
                else -> Idle
            }
        }
    }

    private fun fetch(nextPage: Int?): UIState {
        var currentState: UIState = Loading

        launch {
            nextPage?.let {
                with(fetchPageUseCase.execute(it)) {
                    data.value?.let { result -> currentState = PageLoaded(result) }
                    error.value?.let { error -> currentState = Failure(error) }
                }
            }
        }

        return currentState
    }

    private suspend fun nextPage(): Int? {
        var nextPage: Int? = null

        launch(IO) {
            val lastPageLoaded = state.toList().findLast { it is PageLoaded }
            nextPage = (lastPageLoaded as PageLoaded).data.next
        }

        return nextPage
    }

    private fun launch(
        context: CoroutineContext = Default,
        block: suspend () -> Unit
    ) = viewModelScope.launch(context) { block() }
}

sealed class UIState {
    object Loading : UIState()
    data class PageLoaded(val data: Page) : UIState()
    data class Failure(val error: Throwable) : UIState()
    object Idle : UIState()
}

sealed class Action {
    object Init : Action()
    object FetchNext : Action()
    data class Search(val query: String) : Action()
    object CloseFailureDialog : Action()
    object ScrollDown : Action()
}