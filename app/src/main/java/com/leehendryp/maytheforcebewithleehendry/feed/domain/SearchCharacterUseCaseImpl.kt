package com.leehendryp.maytheforcebewithleehendry.feed.domain

import javax.inject.Inject

class SearchCharacterUseCaseImpl
@Inject constructor(private val peopleRepository: PeopleRepository) : SearchCharacterUseCase {
    override suspend fun execute(query: String): Page = peopleRepository.searchCharacterBy(query)
}