package com.leehendryp.maytheforcebewithleehendry.feed.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.leehendryp.maytheforcebewithleehendry.core.MainCoroutineRule
import com.leehendryp.maytheforcebewithleehendry.core.utils.NetworkUtils
import com.leehendryp.maytheforcebewithleehendry.feed.data.remote.RemoteDataSource
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
class PeopleRepositoryImplTest {
    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val networkUtils: NetworkUtils = mockk()
    private val localDataSource: LocalDataSource = spyk()
    private val remoteDataSource: RemoteDataSource = spyk()

    private lateinit var repo: PeopleRepositoryImpl

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
}