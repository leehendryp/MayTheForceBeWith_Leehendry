package com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.leehendryp.maytheforcebewithleehendry.core.MainCoroutineRule
import com.leehendryp.maytheforcebewithleehendry.feed.domain.Character
import com.leehendryp.maytheforcebewithleehendry.feed.domain.FetchPeopleUseCase
import com.leehendryp.maytheforcebewithleehendry.feed.domain.People
import com.leehendryp.maytheforcebewithleehendry.feed.domain.SearchCharacterUseCase
import com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel.FeedState.Default
import com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel.FeedState.Loading
import com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel.FeedState.Success
import com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel.FeedState.Error
import com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel.FeedState.Search
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import io.mockk.verifyOrder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

// FIXME: Lee Mar 2st, 2020: runBlockingTest API has a known issue (#1204 and #1626). Refactor tests when API is fixed
// For more info, check: https://github.com/Kotlin/kotlinx.coroutines/issues/1204
// For more info, check: https://github.com/Kotlin/kotlinx.coroutines/issues/1626

@ExperimentalCoroutinesApi
class FeedViewModelTest {
    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var viewModel: FeedViewModel

    private val fetchPeopleUseCase: FetchPeopleUseCase = mockk()
    private val searchCharacterUseCase: SearchCharacterUseCase = mockk()

    private lateinit var mockedObserver: Observer<FeedState>

    private val dummy = Character(
        name = "Anakin Skywalker",
        height = "188",
        mass = "84",
        hairColor = "blond",
        skinColor = "fair",
        eyeColor = "blue",
        birthYear = "41.9BBY",
        gender = "male",
        id = 11
    )

    private val dummies = People(1, 3, listOf(dummy).sortedBy { it.id })

    @Before
    @Test
    fun `set up`() {
        viewModel = FeedViewModel(fetchPeopleUseCase, searchCharacterUseCase)
        createMockedObserver()
        observeViewModelState()
    }

    private fun createMockedObserver() {
        mockedObserver = spyk(Observer { Unit })
    }

    private fun observeViewModelState() {
        viewModel.state.observeForever(mockedObserver)
    }

    @Test
    fun `should update state to success with data and then to default upon successful fetch use case execution`() =
        runBlocking {
            val stateSlots = mutableListOf<FeedState>()
            coEvery { fetchPeopleUseCase.execute(1) } returns dummies

            viewModel.fetchPeople()

            verify(exactly = 4) { mockedObserver.onChanged(capture(stateSlots)) }

            verifyOrder {
                mockedObserver.onChanged(Default)
                mockedObserver.onChanged(Loading)
                mockedObserver.onChanged(any<Success>())
                mockedObserver.onChanged(Default)
            }

            assertThat((stateSlots[2] as Success).data, equalTo(dummies.people))
        }

    @Test
    fun `should update state to error and then to default upon failed fetch use case execution`() =
        runBlocking {
            val error = Throwable()
            val stateSlots = mutableListOf<FeedState>()

            coEvery { fetchPeopleUseCase.execute(1) } throws error

            viewModel.fetchPeople()

            verify(exactly = 4) { mockedObserver.onChanged(capture(stateSlots)) }

            verifyOrder {
                mockedObserver.onChanged(Default)
                mockedObserver.onChanged(Loading)
                mockedObserver.onChanged(any<Error>())
                mockedObserver.onChanged(Default)
            }

            assertThat((stateSlots[2] as Error).error, equalTo(error))
        }

    @Test
    fun `should update state to success with data and then to default upon successful search use case execution`() =
        runBlocking {
            val stateSlots = mutableListOf<FeedState>()
            coEvery { searchCharacterUseCase.execute(any()) } returns dummies

            viewModel.searchCharacterBy("")

            verify(exactly = 5) { mockedObserver.onChanged(capture(stateSlots)) }

            verifyOrder {
                mockedObserver.onChanged(Default)
                mockedObserver.onChanged(Search)
                mockedObserver.onChanged(Loading)
                mockedObserver.onChanged(any<Success>())
                mockedObserver.onChanged(Default)
            }

            assertThat((stateSlots[3] as Success).data, equalTo(dummies.people))
        }

    @Test
    fun `should update state to error and then to default upon failed search use case execution`() =
        runBlocking {
            val error = Throwable()
            val stateSlots = mutableListOf<FeedState>()

            coEvery { searchCharacterUseCase.execute(any()) } throws error

            viewModel.searchCharacterBy("")

            verify(exactly = 5) { mockedObserver.onChanged(capture(stateSlots)) }

            verifyOrder {
                mockedObserver.onChanged(Default)
                mockedObserver.onChanged(Search)
                mockedObserver.onChanged(Loading)
                mockedObserver.onChanged(any<Error>())
                mockedObserver.onChanged(Default)
            }

            assertThat((stateSlots[3] as Error).error, equalTo(error))
        }
}