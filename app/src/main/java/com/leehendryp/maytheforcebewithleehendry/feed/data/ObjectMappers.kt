package com.leehendryp.maytheforcebewithleehendry.feed.data

import com.leehendryp.maytheforcebewithleehendry.core.utils.UriParser.parseToId
import com.leehendryp.maytheforcebewithleehendry.core.utils.UriParser.parseToPageNumber
import com.leehendryp.maytheforcebewithleehendry.feed.data.entities.CharacterResponse
import com.leehendryp.maytheforcebewithleehendry.feed.data.entities.PeopleResponse
import com.leehendryp.maytheforcebewithleehendry.feed.domain.Character
import com.leehendryp.maytheforcebewithleehendry.feed.domain.People

private const val NO_ID = -1

fun CharacterResponse.toCharacter() = Character(
    name = name ?: "",
    height = height ?: "",
    mass = mass ?: "",
    hairColor = hairColor ?: "",
    skinColor = skinColor ?: "",
    eyeColor = eyeColor ?: "",
    birthYear = birthYear ?: "",
    gender = gender ?: "",
    id = url?.let { parseToId(it) } ?: NO_ID
)

fun PeopleResponse.toPeople() = People(
    count = count ?: 0,
    next = next?.let { parseToPageNumber(it) },
    people = results?.toCharacterList() ?: listOf()
)

fun List<CharacterResponse>.toCharacterList(): List<Character> {
    val mutableList = mutableListOf<Character>()
    forEach { mutableList.add(it.toCharacter()) }
    return mutableList.toList()
}