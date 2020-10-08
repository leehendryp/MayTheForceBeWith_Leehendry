package com.leehendryp.maytheforcebewithleehendry.feed.data.remote

import com.leehendryp.maytheforcebewithleehendry.feed.domain.Character
import com.leehendryp.maytheforcebewithleehendry.feed.domain.People

interface RemoteDataSource {
    suspend fun fetchPeople(page: Int): Resource<People, Throwable>
    suspend fun searchCharacterBy(name: String): People
    suspend fun saveFavorite(character: Character)
}