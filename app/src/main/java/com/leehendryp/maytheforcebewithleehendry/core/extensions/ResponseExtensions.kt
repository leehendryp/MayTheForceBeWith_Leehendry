package com.leehendryp.maytheforcebewithleehendry.core.extensions

import com.leehendryp.maytheforcebewithleehendry.core.Resource
import com.leehendryp.maytheforcebewithleehendry.core.ClientException
import com.leehendryp.maytheforcebewithleehendry.core.ServiceException
import com.leehendryp.maytheforcebewithleehendry.core.ShouldNotBeAnException
import retrofit2.Response

fun <T, S> Response<T>.handle(map: ((T) -> S)?): Resource<S> {
    return Resource<S>().apply {
        if (isSuccessful) map?.let { map -> body()?.let { setData(map(it)) } }
        else setError(throwable())
    }
}

fun Response<*>.throwable(): Throwable {
    return with(code()) {
        when {
            this in 100..300 -> ShouldNotBeAnException(message() ?: "")
            this in 400..499 -> ClientException(message() ?: "")
            this >= 500 -> ServiceException(message() ?: "")
            else -> Exception(message() ?: "")
        }
    }
}