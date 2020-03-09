package com.leehendryp.maytheforcebewithleehendry.feed.data.remote

import com.leehendryp.maytheforcebewithleehendry.core.StarWarsApi
import com.leehendryp.maytheforcebewithleehendry.core.WebhookApi
import com.leehendryp.maytheforcebewithleehendry.core.utils.coTryCatch
import com.leehendryp.maytheforcebewithleehendry.feed.data.CouldNotFetchPeopleError
import com.leehendryp.maytheforcebewithleehendry.feed.data.CouldNotSaveFavoriteCharacterError
import com.leehendryp.maytheforcebewithleehendry.feed.data.CouldNotSearchCharacterError
import com.leehendryp.maytheforcebewithleehendry.feed.data.toPeople
import com.leehendryp.maytheforcebewithleehendry.feed.domain.Character
import com.leehendryp.maytheforcebewithleehendry.feed.domain.People
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(
    private val starWarsApi: StarWarsApi,
    private val webhookApi: WebhookApi
) : RemoteDataSource {
    companion object {
        private const val REMOTE_SOURCE_ERROR = "Failed to fetch data from remote source"
    }

    override suspend fun fetchPeople(page: Int): People = coTryCatch(
        { starWarsApi.fetchPeople(page).toPeople() },
        { CouldNotFetchPeopleError(REMOTE_SOURCE_ERROR, it) }
    )

    override suspend fun searchCharacterBy(name: String): People = coTryCatch(
        { starWarsApi.searchCharacterBy(name).toPeople() },
        { CouldNotSearchCharacterError(REMOTE_SOURCE_ERROR, it) }
    )

    override suspend fun saveFavorite(character: Character) = coTryCatch(
        { webhookApi.saveFavorite(character) },
        { CouldNotSaveFavoriteCharacterError(REMOTE_SOURCE_ERROR, it) }
    )
}