package com.leehendryp.maytheforcebewithleehendry.feed.domain

import com.leehendryp.maytheforcebewithleehendry.core.Resource

interface PeopleRepository {
    suspend fun fetchPeople(page: Int): Resource<Page>
    suspend fun searchCharacterBy(name: String): Page
    suspend fun save(page: Page)
    suspend fun saveFavorite(character: Character)
}