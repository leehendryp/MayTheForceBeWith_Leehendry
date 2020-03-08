package com.leehendryp.maytheforcebewithleehendry.feed.data

import com.leehendryp.maytheforcebewithleehendry.feed.domain.People
import com.leehendryp.maytheforcebewithleehendry.feed.domain.Character

interface LocalDataSource {
    suspend fun fetchPeople(): People

    suspend fun fetchCharacterBy(id: Int): Character

    suspend fun searchCharacterBy(name: String): People

    suspend fun save(people: People)
}