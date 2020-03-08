package com.leehendryp.maytheforcebewithleehendry.feed.domain

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.leehendryp.maytheforcebewithleehendry.core.MainCoroutineRule
import io.mockk.coVerify
import io.mockk.spyk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SearchCharacterUseCaseImplTest {
    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var repo: PeopleRepository
    private lateinit var useCase: SearchCharacterUseCase

    @Before
    fun `set up`() {
        repo = spyk()
        useCase = SearchCharacterUseCaseImpl(repo)
    }

    @Test
    fun `should call repository with the correct query upon call`() = runBlocking {
        useCase.execute("Rey")

        coVerify(exactly = 0) { repo.searchCharacterBy("Leia") }
        coVerify(exactly = 1) { repo.searchCharacterBy("Rey") }
    }
}