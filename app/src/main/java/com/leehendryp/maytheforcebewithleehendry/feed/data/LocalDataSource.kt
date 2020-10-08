package com.leehendryp.maytheforcebewithleehendry.feed.data

import com.leehendryp.maytheforcebewithleehendry.feed.data.remote.Resource
import com.leehendryp.maytheforcebewithleehendry.feed.domain.People

interface LocalDataSource {
    suspend fun fetchPeople(): Resource<People, Throwable>

    suspend fun searchCharacterBy(name: String): People

    suspend fun save(people: People)
}