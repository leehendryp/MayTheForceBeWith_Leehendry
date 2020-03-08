package com.leehendryp.maytheforcebewithleehendry.feed.data.local

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.leehendryp.maytheforcebewithleehendry.feed.domain.Character

@Entity
data class RoomPeople(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    @NonNull @ColumnInfo(name = "people") val people: List<Character>
)