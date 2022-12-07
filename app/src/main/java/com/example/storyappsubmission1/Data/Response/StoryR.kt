package com.example.storyappsubmission1.Data.Response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class StoryR(
    @field:SerializedName("listStory") val listStory: List<ListStoryR>,
    @field:SerializedName("error") val error: Boolean,
    @field:SerializedName("message") val message: String
)

@Parcelize
data class ListStoryR(
    @field:SerializedName("photoUrl") val photoUrl: String,
    @field:SerializedName("createdAt") val post: String,
    @field:SerializedName("name") val name: String,
    @field:SerializedName("description") val description: String,
    @field:SerializedName("lon") val lon: Double,
    @field:SerializedName("id") val id: String,
    @field:SerializedName("lat") val lat: Double
): Parcelable


