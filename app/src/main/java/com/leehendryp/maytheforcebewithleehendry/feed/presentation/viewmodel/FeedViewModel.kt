package com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leehendryp.maytheforcebewithleehendry.feed.domain.Character
import com.leehendryp.maytheforcebewithleehendry.feed.domain.FetchPeopleUseCase
import com.leehendryp.maytheforcebewithleehendry.feed.domain.SaveFavoriteUseCase
import com.leehendryp.maytheforcebewithleehendry.feed.domain.SearchCharacterUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class FeedViewModel @Inject constructor(
    private val fetchPeopleUseCase: FetchPeopleUseCase,
    private val searchCharacterUseCase: SearchCharacterUseCase,
    private val saveFavoriteUseCase: SaveFavoriteUseCase
) : ViewModel() {
    private val _state by lazy { MutableLiveData<FeedState>().apply { toDefault() } }
    val state: LiveData<FeedState> = _state

    private var nextPage: Int? = 1

    init {
        fetchPeople()
    }

    fun fetchPeople() {
        nextPage?.let {
            launchDataLoad {
                with(fetchPeopleUseCase.execute(it)) {
                    nextPage = next
                    _state.toSuccess(this.people)
                }
            }
        }
    }

    fun searchCharacterBy(name: String) {
        _state.toSearch()

        launchDataLoad {
            with(searchCharacterUseCase.execute(name)) {
                _state.toSuccess(this.people)
            }
        }

        resetPage()
    }

    fun saveFavorite(character: Character) = launchDataLoad {
        saveFavoriteUseCase.execute(character)
    }

    private fun resetPage() {
        nextPage = 1
    }

    private fun <T> launchDataLoad(block: suspend () -> T): Job {
        _state.toLoading()
        return viewModelScope.launch {
            try {
                block()
            } catch (error: Throwable) {
                _state.toError(error)
            } finally {
                _state.toDefault()
            }
        }
    }
}