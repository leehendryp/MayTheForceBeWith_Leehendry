package com.leehendryp.maytheforcebewithleehendry.feed.data.remote

import com.leehendryp.maytheforcebewithleehendry.core.Resource
import com.leehendryp.maytheforcebewithleehendry.core.StarWarsApi
import com.leehendryp.maytheforcebewithleehendry.core.WebhookApi
import com.leehendryp.maytheforcebewithleehendry.core.extensions.handle
import com.leehendryp.maytheforcebewithleehendry.core.utils.coTryCatch
import com.leehendryp.maytheforcebewithleehendry.feed.data.CouldNotSaveFavoriteCharacterError
import com.leehendryp.maytheforcebewithleehendry.feed.data.entities.PageResponse
import com.leehendryp.maytheforcebewithleehendry.feed.data.toPeople
import com.leehendryp.maytheforcebewithleehendry.feed.domain.Character
import com.leehendryp.maytheforcebewithleehendry.feed.domain.Page
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(
    private val starWarsApi: StarWarsApi,
    private val webhookApi: WebhookApi
) : RemoteDataSource {
    companion object {
        private const val REMOTE_SOURCE_ERROR = "Failed to fetch data from remote source"
    }

    override suspend fun fetchPeople(page: Int): Resource<Page> {
        return starWarsApi.fetchPeople(page)
            .resource()
    }

    override suspend fun searchCharacterBy(name: String): Resource<Page> {
        return starWarsApi.searchCharacterBy(name)
            .resource()
    }

    override suspend fun saveFavorite(character: Character) = coTryCatch(
        { webhookApi.saveFavorite(character) },
        { CouldNotSaveFavoriteCharacterError(REMOTE_SOURCE_ERROR, it) }
    )

    private fun Response<PageResponse>.resource(): Resource<Page> {
        return try {
            handle { response -> response.toPeople() }
        } catch (error: Throwable) {
            Resource<Page>()
                .apply { setError(error) }
        }
    }
}