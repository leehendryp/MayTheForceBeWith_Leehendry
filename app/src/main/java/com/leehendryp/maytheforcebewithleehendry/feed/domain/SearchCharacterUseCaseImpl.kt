package com.leehendryp.maytheforcebewithleehendry.feed.domain

import com.leehendryp.maytheforcebewithleehendry.core.Resource
import javax.inject.Inject

class SearchCharacterUseCaseImpl
@Inject constructor(private val peopleRepository: PeopleRepository) : SearchCharacterUseCase {
    override suspend fun execute(query: String): Resource<Page> = peopleRepository.searchCharacterBy(query)
}