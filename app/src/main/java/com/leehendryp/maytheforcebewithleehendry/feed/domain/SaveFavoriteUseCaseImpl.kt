package com.leehendryp.maytheforcebewithleehendry.feed.domain

class SaveFavoriteUseCaseImpl(private val repository: PeopleRepository) : SaveFavoriteUseCase {
    override suspend fun execute(character: Character) = repository.saveFavorite(character)
}