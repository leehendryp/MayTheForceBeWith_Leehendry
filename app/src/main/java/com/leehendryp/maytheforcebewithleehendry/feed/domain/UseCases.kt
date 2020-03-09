package com.leehendryp.maytheforcebewithleehendry.feed.domain

interface FetchPeopleUseCase {
    suspend fun execute(page: Int): People
}

interface SearchCharacterUseCase {
    suspend fun execute(query: String): People
}

interface SaveFavoriteUseCase {
    suspend fun execute(character: Character)
}