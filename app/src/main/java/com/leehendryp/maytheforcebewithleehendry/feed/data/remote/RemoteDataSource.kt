package com.leehendryp.maytheforcebewithleehendry.feed.data.remote

import com.leehendryp.maytheforcebewithleehendry.core.Resource
import com.leehendryp.maytheforcebewithleehendry.feed.domain.Character
import com.leehendryp.maytheforcebewithleehendry.feed.domain.Page

interface RemoteDataSource {
    suspend fun fetchPeople(page: Int): Resource<Page>
    suspend fun searchCharacterBy(name: String): Resource<Page>
    suspend fun saveFavorite(character: Character)
}