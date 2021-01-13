package com.leehendryp.maytheforcebewithleehendry.feed.domain

import com.leehendryp.maytheforcebewithleehendry.core.Resource

interface FetchPeopleUseCase {
    suspend fun execute(page: Int): Resource<Page>
}

interface SearchCharacterUseCase {
    suspend fun execute(query: String): Resource<Page>
}

interface SaveFavoriteUseCase {
    suspend fun execute(character: Character)
}