package com.leehendryp.maytheforcebewithleehendry.feed.domain

import com.leehendryp.maytheforcebewithleehendry.feed.data.remote.Resource
import javax.inject.Inject

class FetchPeopleUseCaseImpl
@Inject constructor(private val peopleRepository: PeopleRepository) : FetchPeopleUseCase {
    override suspend fun execute(page: Int): Resource<Page> = peopleRepository.fetchPeople(page)
}