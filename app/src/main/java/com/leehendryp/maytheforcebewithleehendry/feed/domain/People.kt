package com.leehendryp.maytheforcebewithleehendry.feed.domain

data class People(
    val count: Int,
    val next: Int? = null,
    val people: List<Character>
)