package com.leehendryp.maytheforcebewithleehendry.feed.data.local.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.leehendryp.maytheforcebewithleehendry.feed.domain.Character
import java.lang.reflect.Type

class Converters {
    @TypeConverter
    fun fromCharacterList(characterList: List<Character>): String = Gson().toJson(characterList)

    @TypeConverter
    fun toCharacterList(value: String): List<Character> {
        val listType = object : TypeToken<List<Character>>() {}
            .type as Type
        return Gson().fromJson(value, listType)
    }
}