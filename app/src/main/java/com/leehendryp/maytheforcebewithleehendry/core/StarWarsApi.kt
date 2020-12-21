package com.leehendryp.maytheforcebewithleehendry.core

import com.leehendryp.maytheforcebewithleehendry.feed.data.entities.PeopleResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

const val STAR_WARS_BASE_URL = "https://swapi.dev/api/"
private const val PEOPLE_ENDPOINT = "people/"

interface StarWarsApi {
    @GET(PEOPLE_ENDPOINT)
    suspend fun fetchPeople(
        @Query("page") page: Int
    ): Response<PeopleResponse>

    @GET(PEOPLE_ENDPOINT)
    suspend fun searchCharacterBy(
        @Query("search") name: String
    ): Response<PeopleResponse>
}