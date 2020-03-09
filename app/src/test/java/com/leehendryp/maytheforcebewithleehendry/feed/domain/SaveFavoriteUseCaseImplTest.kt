package com.leehendryp.maytheforcebewithleehendry.feed.domain

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.leehendryp.maytheforcebewithleehendry.core.MainCoroutineRule
import io.mockk.coVerify
import io.mockk.spyk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SaveFavoriteUseCaseImplTest {
    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var repo: PeopleRepository
    private lateinit var useCase: SaveFavoriteUseCase

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

    @Before
    fun `set up`() {
        repo = spyk()
        useCase = SaveFavoriteUseCaseImpl(repo)
    }

    @Test
    fun `should call repository upon save favorite call`() = runBlocking {
        useCase.execute(dummy)

        coVerify(exactly = 1) { repo.saveFavorite(dummy) }
    }
}