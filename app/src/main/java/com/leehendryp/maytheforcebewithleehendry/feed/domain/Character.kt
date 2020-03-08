package com.leehendryp.maytheforcebewithleehendry.feed.domain

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Character(
    @SerializedName("name") val name: String,
    @SerializedName("height") val height: String,
    @SerializedName("mass") val mass: String,
    @SerializedName("hair_color") val hairColor: String,
    @SerializedName("skin_color") val skinColor: String,
    @SerializedName("eye_Color") val eyeColor: String,
    @SerializedName("birth_year") val birthYear: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("id") val id: Int
) : Serializable {
    companion object {
        const val CHARACTER = "CHARACTER"
    }
}