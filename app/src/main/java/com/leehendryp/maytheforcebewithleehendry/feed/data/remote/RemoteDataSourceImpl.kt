package com.leehendryp.maytheforcebewithleehendry.feed.data.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.leehendryp.maytheforcebewithleehendry.core.StarWarsApi
import com.leehendryp.maytheforcebewithleehendry.core.WebhookApi
import com.leehendryp.maytheforcebewithleehendry.core.utils.coTryCatch
import com.leehendryp.maytheforcebewithleehendry.feed.data.CouldNotSaveFavoriteCharacterError
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
            .handle { response -> response.toPeople() }
    }

    override suspend fun searchCharacterBy(name: String): Page = Page(1, 1, listOf())

    override suspend fun saveFavorite(character: Character) = coTryCatch(
        { webhookApi.saveFavorite(character) },
        { CouldNotSaveFavoriteCharacterError(REMOTE_SOURCE_ERROR, it) }
    )
}

class Resource<T> {
    private val _data by lazy { MutableLiveData<T>() }
    val data: LiveData<T> = _data

    private val _error by lazy { MutableLiveData<Throwable>() }
    val error: LiveData<Throwable> = _error

    fun setData(data: T) {
        _data.value = data
    }

    fun setError(error: Throwable) {
        _error.value = error
    }
}

fun <T, S> Response<T>.handle(map: ((T) -> S)?): Resource<S> {
    return Resource<S>().apply {
        if (isSuccessful) map?.let { map -> body()?.let { setData(map(it)) } }
        else setError(throwable())
    }
}

fun Response<*>.throwable(): Throwable {
    return with(code()) {
        when {
            this in 100..300 -> ShouldNotBeAnException("")
            this in 400..499 -> ClientException(message() ?: "")
            this >= 500 -> ServiceException(message() ?: "")
            else -> Exception(message() ?: "")
        }
    }
}

class ShouldNotBeAnException(message: String) : Throwable(message)
class ClientException(message: String) : Throwable(message)
class ServiceException(message: String) : Throwable(message)