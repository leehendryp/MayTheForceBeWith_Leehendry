package com.leehendryp.maytheforcebewithleehendry.feed.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.leehendryp.maytheforcebewithleehendry.core.MainCoroutineRule
import com.leehendryp.maytheforcebewithleehendry.core.utils.NetworkUtils
import com.leehendryp.maytheforcebewithleehendry.feed.data.remote.RemoteDataSource
import com.leehendryp.maytheforcebewithleehendry.feed.domain.Character
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.lang.Exception

// FIXME: Lee Mar 2st, 2020: runBlockingTest API has a known issue (#1204 and #1626). Refactor tests when API is fixed
// For more info, check: https://github.com/Kotlin/kotlinx.coroutines/issues/1204
// For more info, check: https://github.com/Kotlin/kotlinx.coroutines/issues/1626

@ExperimentalCoroutinesApi
class PageRepositoryImplTest {
    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val networkUtils: NetworkUtils = mockk()
    private val localDataSource: LocalDataSource = spyk()
    private val remoteDataSource: RemoteDataSource = spyk()

    private lateinit var repo: PeopleRepositoryImpl

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
    @Test
    fun `set up`() {
        repo = PeopleRepositoryImpl(networkUtils, localDataSource, remoteDataSource)
    }

    @Test
    fun `should fetch data from remote source if network is available`() =
        coroutineRule.runBlockingTest {
            every { networkUtils.isInternetAvailable() } returns true

            repo.fetchPeople(1)

            coVerify(exactly = 0) { localDataSource.fetchPeople() }
            coVerify(exactly = 1) { remoteDataSource.fetchPeople(1) }
        }

    @Test
    fun `should fetch data from local source if network is unavailable`() =
        coroutineRule.runBlockingTest {
            every { networkUtils.isInternetAvailable() } returns false

            repo.fetchPeople(1)

            coVerify(exactly = 1) { localDataSource.fetchPeople() }
            coVerify(exactly = 0) { remoteDataSource.fetchPeople(1) }
        }

    @Test
    fun `should fetch data from local source if remote data fetch fails`() =
        coroutineRule.runBlockingTest {
            every { networkUtils.isInternetAvailable() } returns true
            coEvery { remoteDataSource.fetchPeople(1) } throws Exception()

            repo.fetchPeople(1)

            coVerifyOrder {
                remoteDataSource.fetchPeople(1)
                localDataSource.fetchPeople()
            }
        }

    @Test
    fun `should save data from remote source to local one upon successful request`() =
        coroutineRule.runBlockingTest {
            every { networkUtils.isInternetAvailable() } returns true

            repo.fetchPeople(1)

            coVerify(exactly = 0) { localDataSource.fetchPeople() }
            coVerify(exactly = 1) { remoteDataSource.fetchPeople(1) }
            coVerify(exactly = 1) { repo.save(any()) }
            coVerify(exactly = 1) { localDataSource.save(any()) }
        }

    @Test
    fun `should matching character query data from remote source if network is available`() =
        coroutineRule.runBlockingTest {
            every { networkUtils.isInternetAvailable() } returns true

            repo.searchCharacterBy("")

            coVerify(exactly = 0) { localDataSource.searchCharacterBy(any()) }
            coVerify(exactly = 1) { remoteDataSource.searchCharacterBy(any()) }
        }

    @Test
    fun `should matching character query data from local source if network is unavailable`() =
        coroutineRule.runBlockingTest {
            every { networkUtils.isInternetAvailable() } returns false

            repo.searchCharacterBy("")

            coVerify(exactly = 1) { localDataSource.searchCharacterBy(any()) }
            coVerify(exactly = 0) { remoteDataSource.searchCharacterBy(any()) }
        }

    @Test
    fun `should matching character query data from local source if remote fetch fails`() =
        coroutineRule.runBlockingTest {
            every { networkUtils.isInternetAvailable() } returns true
            coEvery { remoteDataSource.searchCharacterBy(any()) } throws Exception()

            repo.searchCharacterBy("")

            coVerifyOrder {
                remoteDataSource.searchCharacterBy(any())
                localDataSource.searchCharacterBy(any())
            }
        }

    @Test
    fun `should save favorite character to remote source if internet is available`() =
        coroutineRule.runBlockingTest {
            every { networkUtils.isInternetAvailable() } returns true
            coEvery { remoteDataSource.saveFavorite(any()) } returns Unit

            repo.saveFavorite(dummy)

            coVerify(exactly = 1) { remoteDataSource.saveFavorite(any()) }
        }

    @Test
    fun `should not save favorite character to remote source if internet is unavailable`() =
        coroutineRule.runBlockingTest {
            every { networkUtils.isInternetAvailable() } returns false
            coEvery { remoteDataSource.saveFavorite(any()) } returns Unit

            repo.saveFavorite(dummy)

            coVerify(exactly = 0) { remoteDataSource.saveFavorite(any()) }
        }
}