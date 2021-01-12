package com.leehendryp.maytheforcebewithleehendry.feed.data

import com.leehendryp.maytheforcebewithleehendry.feed.domain.Page

interface LocalDataSource {
    suspend fun fetchPeople(): Page

    suspend fun searchCharacterBy(name: String): Page

    suspend fun save(page: Page)
}