package com.leehendryp.maytheforcebewithleehendry.feed.data.entities

import com.google.gson.annotations.SerializedName

data class PeopleResponse(
    @SerializedName("count") val count: Int?,
    @SerializedName("next") val next: String?,
    @SerializedName("previous") val previous: String?,
    @SerializedName("results") val results: List<CharacterResponse>?
)