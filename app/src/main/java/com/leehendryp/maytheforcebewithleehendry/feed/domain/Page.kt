package com.leehendryp.maytheforcebewithleehendry.feed.domain

data class Page(
    val count: Int,
    val next: Int? = null,
    val characters: List<Character>
)