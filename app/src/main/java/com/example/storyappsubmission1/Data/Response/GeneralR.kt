package com.example.storyappsubmission1.Data.Response

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class GeneralR(
    @field:SerializedName("error") val error: Boolean,
    @field:SerializedName("message") val message: String
)

@Entity(tableName = "story")
@Parcelize
data class StoryModel(

    @PrimaryKey
    val id: String,
    val photoUrl: String,
    val createdAt: String,
    val name: String,
    val description: String,
    val lat: Double,
    val lon: Double
) : Parcelable

@Entity(tableName = "remote_keys")
data class RemoteKeysModel(
    @PrimaryKey val id: String,
    val prevKey: Int?,
    val nextKey: Int?
)
