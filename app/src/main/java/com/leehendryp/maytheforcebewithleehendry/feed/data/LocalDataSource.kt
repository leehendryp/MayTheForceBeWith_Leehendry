package com.leehendryp.maytheforcebewithleehendry.feed.data

import com.leehendryp.maytheforcebewithleehendry.feed.domain.People

interface LocalDataSource {
    suspend fun fetchPeople(): People

    suspend fun searchCharacterBy(name: String): People

    suspend fun save(people: People)
}