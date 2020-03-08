package com.leehendryp.maytheforcebewithleehendry.feed.domain

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.leehendryp.maytheforcebewithleehendry.core.MainCoroutineRule
import io.mockk.coVerify
import io.mockk.spyk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

// FIXME: Lee Mar 2st, 2020: runBlockingTest API has a known issue (#1204 and #1626). Refactor tests when API is fixed
// For more info, check: https://github.com/Kotlin/kotlinx.coroutines/issues/1204
// For more info, check: https://github.com/Kotlin/kotlinx.coroutines/issues/1626

@ExperimentalCoroutinesApi
class FetchPeopleUseCaseImplTest {
    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var repo: PeopleRepository
    private lateinit var useCase: FetchPeopleUseCase

    @Before
    fun `set up`() {
        repo = spyk()
        useCase = FetchPeopleUseCaseImpl(repo)
    }

    @Test
    fun `should call repository with the correct page upon call`() = runBlocking {
        useCase.execute(13)

        coVerify(exactly = 0) { repo.fetchPeople(21) }
        coVerify(exactly = 1) { repo.fetchPeople(13) }
    }
}