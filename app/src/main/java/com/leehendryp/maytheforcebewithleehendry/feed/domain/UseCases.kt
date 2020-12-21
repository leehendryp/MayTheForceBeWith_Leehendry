package com.leehendryp.maytheforcebewithleehendry.feed.domain

import com.leehendryp.maytheforcebewithleehendry.feed.data.remote.Resource

interface FetchPeopleUseCase {
    suspend fun execute(page: Int): Resource<Page>
}

interface SearchCharacterUseCase {
    suspend fun execute(query: String): Page
}

interface SaveFavoriteUseCase {
    suspend fun execute(character: Character)
}