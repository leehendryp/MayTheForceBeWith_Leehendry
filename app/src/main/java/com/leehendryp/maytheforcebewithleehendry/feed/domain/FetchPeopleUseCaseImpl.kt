package com.leehendryp.maytheforcebewithleehendry.feed.domain

import javax.inject.Inject

class FetchPeopleUseCaseImpl
@Inject constructor(private val peopleRepository: PeopleRepository) : FetchPeopleUseCase {
    override suspend fun execute(page: Int): People = peopleRepository.fetchPeople(page)
}