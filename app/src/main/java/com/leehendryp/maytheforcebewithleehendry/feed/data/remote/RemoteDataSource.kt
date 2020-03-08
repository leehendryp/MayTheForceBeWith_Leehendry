package com.leehendryp.maytheforcebewithleehendry.feed.data.remote

import com.leehendryp.maytheforcebewithleehendry.feed.domain.People

interface RemoteDataSource {
    suspend fun fetchPeople(page: Int): People
    suspend fun searchCharacterBy(name: String): People
}