package com.rockytauhid.storyapps.data.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.storyappsubmission1.Data.Response.RemoteKeysModel
import com.example.storyappsubmission1.Data.Response.StoryModel

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(storyModel: List<com.example.storyappsubmission1.Data.Response.StoryModel>)

    @Query("SELECT * FROM story")
    fun getAll(): PagingSource<Int, StoryModel>

    @Query("DELETE FROM story")
    suspend fun deleteAll()
}


@Dao
interface RemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<RemoteKeysModel>)

    @Query("SELECT * FROM remote_keys WHERE id = :id")
    suspend fun getRemoteKeysId(id: String): RemoteKeysModel?

    @Query("DELETE FROM remote_keys")
    suspend fun deleteRemoteKeys()
}