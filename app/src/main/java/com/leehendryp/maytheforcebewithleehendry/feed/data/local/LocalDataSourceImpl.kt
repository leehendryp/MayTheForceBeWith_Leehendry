package com.leehendryp.maytheforcebewithleehendry.feed.data.local

import com.leehendryp.maytheforcebewithleehendry.core.utils.coTryCatch
import com.leehendryp.maytheforcebewithleehendry.core.utils.io
import com.leehendryp.maytheforcebewithleehendry.feed.data.CouldNotFetchCharacterError
import com.leehendryp.maytheforcebewithleehendry.feed.data.CouldNotFetchPeopleError
import com.leehendryp.maytheforcebewithleehendry.feed.data.CouldNotSavePeopleError
import com.leehendryp.maytheforcebewithleehendry.feed.data.CouldNotSearchCharacterError
import com.leehendryp.maytheforcebewithleehendry.feed.data.LocalDataSource
import com.leehendryp.maytheforcebewithleehendry.feed.domain.Character
import com.leehendryp.maytheforcebewithleehendry.feed.domain.People
import javax.inject.Inject

class LocalDataSourceImpl @Inject constructor(
    private val roomPeopleDao: RoomPeopleDao
) : LocalDataSource {
    companion object {
        private const val LOCAL_SOURCE_ERROR = "Failed to fetch data from local source"
    }

    override suspend fun fetchPeople(): People = coTryCatch(
        dispatcher = io(),
        onTry = { People(count = getLocalCharacters().size, people = getLocalCharacters()) },
        onCatch = { CouldNotFetchPeopleError(LOCAL_SOURCE_ERROR, it) }
    )

    override suspend fun fetchCharacterBy(id: Int): Character = coTryCatch(
        dispatcher = io(),
        onTry = { getLocalCharacters().find { character -> character.id == id }!! },
        onCatch = { CouldNotFetchCharacterError(LOCAL_SOURCE_ERROR, it) }
    )

    override suspend fun searchCharacterBy(name: String): People = coTryCatch(
        dispatcher = io(),
        onTry = { getLocalCharactersBy(name) },
        onCatch = { CouldNotSearchCharacterError(LOCAL_SOURCE_ERROR, it) }
    )

    override suspend fun save(people: People) = coTryCatch(
        dispatcher = io(),
        onTry = { roomPeopleDao.insert(RoomPeople(people = people.people)) },
        onCatch = { CouldNotSavePeopleError(LOCAL_SOURCE_ERROR, it) }
    )

    private suspend fun getLocalCharacters(): List<Character> = roomPeopleDao.getAll().people

    private suspend fun getLocalCharactersBy(name: String): People {
        val matchingCharacters = mutableListOf<Character>()
        getLocalCharacters().forEach { if (it.name.contains(name)) matchingCharacters.add(it) }
        return People(count = matchingCharacters.size, people = matchingCharacters)
    }
}