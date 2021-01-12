package com.leehendryp.maytheforcebewithleehendry.feed.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.leehendryp.maytheforcebewithleehendry.core.MainCoroutineRule
import com.leehendryp.maytheforcebewithleehendry.feed.data.CouldNotFetchPeopleError
import com.leehendryp.maytheforcebewithleehendry.feed.data.LocalDataSource
import com.leehendryp.maytheforcebewithleehendry.feed.domain.Character
import com.leehendryp.maytheforcebewithleehendry.feed.domain.Page
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.spyk
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
class LocalDataSourceImplTest {
    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var dataSource: LocalDataSource
    private val roomPeopleDao: RoomPeopleDao = spyk()

    private val dummyOne = Character(
        name = "Anakin Skywalker",
        height = "188",
        mass = "84",
        hairColor = "blond",
        skinColor = "fair",
        eyeColor = "blue",
        birthYear = "41.9BBY",
        gender = "male",
        id = 99999
    )

    private val dummyTwo = Character(
        name = "Wilhuff Tarkin",
        height = "180",
        mass = "unknown",
        hairColor = "auburn, grey",
        skinColor = "fair",
        eyeColor = "blue",
        birthYear = "64BBY",
        gender = "male",
        id = 11111
    )

    private val dummies = Page(
        count = 2,
        characters = listOf(dummyOne, dummyTwo)
    )

    private val roomPeople = RoomPeople(people = dummies.characters)

    @Before
    @Test
    fun `set up`() {
        dataSource = LocalDataSourceImpl(roomPeopleDao)
    }

    @Test
    fun `should fetch people stored locally upon request`() = runBlocking {
        val expectedPeople = Page(count = dummies.characters.size, characters = dummies.characters)

        coEvery { roomPeopleDao.getAll() } returns roomPeople

        val people = dataSource.fetchPeople()

        assertThat(people, equalTo(expectedPeople))
    }

    @Test(expected = CouldNotFetchPeopleError::class)
    fun `should throw exception if local data fetch fails to be fulfilled`() {
        runBlocking {
            coEvery { roomPeopleDao.getAll() } throws Exception()
            dataSource.fetchPeople()
        }
    }

    @Test
    fun `should fetch character from database upon name search`() {
        runBlocking {}
    }

    @Test
    fun `should throw exception if local character name search fails to be fulfilled`() {
        runBlocking {}
    }

    @Test
    fun `should save people to database upon request`() = runBlocking {
        dataSource.save(dummies)
        coVerify { roomPeopleDao.insert(any()) }
    }
}