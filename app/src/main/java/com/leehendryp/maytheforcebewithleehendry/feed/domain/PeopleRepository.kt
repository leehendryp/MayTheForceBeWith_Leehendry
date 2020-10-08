package com.leehendryp.maytheforcebewithleehendry.feed.domain

import com.leehendryp.maytheforcebewithleehendry.feed.data.remote.Resource

interface PeopleRepository {
    suspend fun fetchPeople(page: Int): Resource<People, Throwable>
    suspend fun searchCharacterBy(name: String): People
    suspend fun save(people: People)
    suspend fun saveFavorite(character: Character)
}