package com.leehendryp.maytheforcebewithleehendry.feed.domain

interface PeopleRepository {
    suspend fun fetchPeople(page: Int): People
    suspend fun searchCharacterBy(name: String): People
    suspend fun save(people: People)
}