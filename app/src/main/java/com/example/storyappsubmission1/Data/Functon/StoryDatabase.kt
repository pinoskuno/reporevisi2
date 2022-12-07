package com.example.storyappsubmission1.Data.Functon

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.storyappsubmission1.Data.Response.RemoteKeysModel
import com.example.storyappsubmission1.Data.Response.StoryModel
import com.rockytauhid.storyapps.data.database.RemoteKeysDao
import com.rockytauhid.storyapps.data.database.StoryDao

@Database(
    entities = [StoryModel::class, RemoteKeysModel::class],
    version = 1,
    exportSchema = false
)
abstract class StoryDatabase : RoomDatabase() {

    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        @Volatile
        private var INSTANCE: StoryDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): StoryDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    StoryDatabase::class.java, "story_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}