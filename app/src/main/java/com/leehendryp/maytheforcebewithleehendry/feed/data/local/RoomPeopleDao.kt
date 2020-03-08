package com.leehendryp.maytheforcebewithleehendry.feed.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RoomPeopleDao {
    @Query("SELECT * FROM roompeople")
    suspend fun getAll(): RoomPeople

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(people: RoomPeople)
}