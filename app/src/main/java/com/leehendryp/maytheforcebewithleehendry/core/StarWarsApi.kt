package com.leehendryp.maytheforcebewithleehendry.core

import com.leehendryp.maytheforcebewithleehendry.feed.data.entities.CharacterResponse
import com.leehendryp.maytheforcebewithleehendry.feed.data.entities.PeopleResponse
import retrofit2.http.GET
import retrofit2.http.Query

const val BASE_URL = "https://swapi.co/api/"

interface StarWarsApi {
    @GET("people/")
    suspend fun fetchPeople(
        @Query("page") offset: Int
    ): PeopleResponse

    @GET("people/")
    suspend fun searchCharacterBy(
        @Query("search") name: String
    ): PeopleResponse
}