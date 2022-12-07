package com.example.storyappsubmission1.Data.API

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.storyappsubmission1.Data.Functon.Preference
import com.example.storyappsubmission1.Data.Functon.StoryDatabase
import com.example.storyappsubmission1.Data.Repository.StoryRepo
import com.example.storyappsubmission1.Data.Repository.UserRepo

object Injection {
    fun provideUserRepository(dataStore: DataStore<Preferences>): UserRepo {
        val userPreferences = Preference.getInstance(dataStore)
        val apiService = Config.getApiService()
        return UserRepo.getInstance(userPreferences, apiService)
    }

    fun provideStoryRepository(context: Context): StoryRepo {
        val database = StoryDatabase.getDatabase(context)
        val apiService = Config.getApiService()
        return StoryRepo.getInstance(database, apiService)
    }
}