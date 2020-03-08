package com.leehendryp.maytheforcebewithleehendry.feed.data.remote

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.leehendryp.maytheforcebewithleehendry.core.BaseNetworkTest
import com.leehendryp.maytheforcebewithleehendry.core.MainCoroutineRule
import com.leehendryp.maytheforcebewithleehendry.core.ResponseType.SUCCESS
import com.leehendryp.maytheforcebewithleehendry.core.ResponseType.CLIENT_ERROR
import com.leehendryp.maytheforcebewithleehendry.core.ResponseType.SERVER_ERROR
import com.leehendryp.maytheforcebewithleehendry.core.utils.UriParser
import com.leehendryp.maytheforcebewithleehendry.core.utils.UriParser.parseToId
import com.leehendryp.maytheforcebewithleehendry.core.utils.UriParser.parseToPageNumber
import com.leehendryp.maytheforcebewithleehendry.feed.data.CouldNotFetchPeopleError
import com.leehendryp.maytheforcebewithleehendry.feed.data.CouldNotSearchCharacterError
import com.leehendryp.maytheforcebewithleehendry.feed.domain.Character
import com.leehendryp.maytheforcebewithleehendry.feed.domain.People
import io.mockk.every
import io.mockk.mockkObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test

private const val PEOPLE_JSON = "PeopleResponse.json"
private const val CHARACTER_JSON = "CharacterResponse.json"
private const val SEARCH_JSON = "SearchPeopleResponse.json"

// FIXME: Lee Mar 2st, 2020: runBlockingTest API has a known issue (#1204 and #1626). Refactor tests when API is fixed
// For more info, check: https://github.com/Kotlin/kotlinx.coroutines/issues/1204
// For more info, check: https://github.com/Kotlin/kotlinx.coroutines/issues/1626

@ExperimentalCoroutinesApi
class RemoteDataSourceImplTest : BaseNetworkTest() {
    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var dataSource: RemoteDataSource

    private val mockId = 99999

    private val dummyOne = Character(
        name = "Anakin Skywalker",
        height = "188",
        mass = "84",
        hairColor = "blond",
        skinColor = "fair",
        eyeColor = "blue",
        birthYear = "41.9BBY",
        gender = "male",
        id = mockId
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
        id = mockId
    )

    private val dummies = People(
        count = 2,
        next = 3,
        people = listOf(dummyOne, dummyTwo)
    )

    @Test
    override fun setUp() {
        super.setUp()
        dataSource = RemoteDataSourceImpl(api)
    }

    @Test
    fun `should fetch people from API upon successful request`() = runBlocking {
        mockkObject(UriParser)
        every { parseToId(any()) } returns mockId
        every { parseToPageNumber(any()) } returns 3

        setResponse(SUCCESS, PEOPLE_JSON)

        val result: People = dataSource.fetchPeople(2)

        assertThat(result, equalTo(dummies))
    }

    @Test(expected = CouldNotFetchPeopleError::class)
    fun `should throw exception if it fails to properly request a people response from API`() {
        runBlocking {
            setResponse(CLIENT_ERROR)
            dataSource.fetchPeople(1)
        }
    }

    @Test(expected = CouldNotFetchPeopleError::class)
    fun `should throw exception if there is a server error upon people request`() {
        runBlocking {
            setResponse(SERVER_ERROR)
            dataSource.fetchPeople(1)
        }
    }

    @Test
    fun `should fetch search people from API upon successful request`() {
        runBlocking {}
    }

    @Test(expected = CouldNotSearchCharacterError::class)
    fun `should throw exception if it fails to properly request a search response from API`() {
        runBlocking {
            setResponse(CLIENT_ERROR)
            dataSource.searchCharacterBy("")
        }
    }

    @Test(expected = CouldNotSearchCharacterError::class)
    fun `should throw exception if there is a server error upon search request`() {
        runBlocking {
            setResponse(SERVER_ERROR)
            dataSource.searchCharacterBy("")
        }
    }
}