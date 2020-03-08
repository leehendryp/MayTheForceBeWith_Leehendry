package com.leehendryp.maytheforcebewithleehendry.feed.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.leehendryp.maytheforcebewithleehendry.feed.data.local.RoomPeople
import com.leehendryp.maytheforcebewithleehendry.feed.data.local.RoomPeopleDao

@Database(entities = [RoomPeople::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class RoomPeopleDatabase : RoomDatabase() {

    abstract fun roomPeopleDao(): RoomPeopleDao

    companion object {
        private const val DATABASE = "people_db"

        @Volatile
        private var INSTANCE: RoomPeopleDatabase? = null

        fun getDatabase(context: Context): RoomPeopleDatabase {
            val tempInstance =
                INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RoomPeopleDatabase::class.java,
                    DATABASE
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}