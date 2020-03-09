package com.leehendryp.maytheforcebewithleehendry.feed.data

import com.leehendryp.maytheforcebewithleehendry.core.utils.NetworkUtils
import com.leehendryp.maytheforcebewithleehendry.feed.data.remote.RemoteDataSource
import com.leehendryp.maytheforcebewithleehendry.feed.domain.Character
import com.leehendryp.maytheforcebewithleehendry.feed.domain.People
import com.leehendryp.maytheforcebewithleehendry.feed.domain.PeopleRepository
import javax.inject.Inject

class PeopleRepositoryImpl @Inject constructor(
    private val networkUtils: NetworkUtils,
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) : PeopleRepository {
    override suspend fun fetchPeople(page: Int): People {
        return if (networkUtils.isInternetAvailable()) {
            try {
                remoteDataSource.fetchPeople(page).also { save(it) }
            } catch (error: Throwable) {
                localDataSource.fetchPeople()
            }
        } else {
            localDataSource.fetchPeople()
        }
    }

    override suspend fun searchCharacterBy(name: String): People {
        return if (networkUtils.isInternetAvailable()) {
            try {
                remoteDataSource.searchCharacterBy(name)
            } catch (error: Throwable) {
                localDataSource.searchCharacterBy(name)
            }
        } else localDataSource.searchCharacterBy(name)
    }

    override suspend fun save(people: People) = localDataSource.save(people)

    override suspend fun saveFavorite(character: Character) {
        if (networkUtils.isInternetAvailable()) remoteDataSource.saveFavorite(character)
    }
}