package com.leehendryp.maytheforcebewithleehendry.feed.data.local

import com.leehendryp.maytheforcebewithleehendry.core.utils.coTryCatch
import com.leehendryp.maytheforcebewithleehendry.feed.data.CouldNotFetchPeopleError
import com.leehendryp.maytheforcebewithleehendry.feed.data.CouldNotSavePeopleError
import com.leehendryp.maytheforcebewithleehendry.feed.data.CouldNotSearchCharacterError
import com.leehendryp.maytheforcebewithleehendry.feed.data.LocalDataSource
import com.leehendryp.maytheforcebewithleehendry.feed.domain.Character
import com.leehendryp.maytheforcebewithleehendry.feed.domain.Page
import kotlinx.coroutines.Dispatchers.IO
import javax.inject.Inject

class LocalDataSourceImpl @Inject constructor(
    private val roomPeopleDao: RoomPeopleDao
) : LocalDataSource {
    companion object {
        private const val LOCAL_SOURCE_ERROR = "Failed to fetch data from local source"
    }

    override suspend fun fetchPeople(): Page = coTryCatch(
        dispatcher = IO,
        onTry = { Page(count = getLocalCharacters().size, characters = getLocalCharacters()) },
        onCatch = { CouldNotFetchPeopleError(LOCAL_SOURCE_ERROR, it) }
    )

    override suspend fun searchCharacterBy(name: String): Page = coTryCatch(
        dispatcher = IO,
        onTry = { getLocalCharactersBy(name) },
        onCatch = { CouldNotSearchCharacterError(LOCAL_SOURCE_ERROR, it) }
    )

    override suspend fun save(page: Page) = coTryCatch(
        dispatcher = IO,
        onTry = { roomPeopleDao.insert(RoomPeople(people = page.characters)) },
        onCatch = { CouldNotSavePeopleError(LOCAL_SOURCE_ERROR, it) }
    )

    private suspend fun getLocalCharacters(): List<Character> = roomPeopleDao.getAll().people

    private suspend fun getLocalCharactersBy(name: String): Page {
        val matchingCharacters = mutableListOf<Character>()
        getLocalCharacters().forEach { if (it.name.contains(name)) matchingCharacters.add(it) }
        return Page(count = matchingCharacters.size, characters = matchingCharacters)
    }
}