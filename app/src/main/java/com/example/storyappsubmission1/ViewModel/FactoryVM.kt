package com.example.storyappsubmission1.ViewModel

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyappsubmission1.Data.API.Injection
import com.example.storyappsubmission1.Data.Repository.StoryRepo
import com.example.storyappsubmission1.Data.Repository.UserRepo


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class FactoryVM (
    private val _userRepo: UserRepo,
    private val _storyRepo: StoryRepo
    ) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainVM::class.java) -> {
                MainVM(_userRepo, _storyRepo) as T
            }
            modelClass.isAssignableFrom(RegisterVM::class.java) -> {
                RegisterVM(_userRepo) as T
            }
            modelClass.isAssignableFrom(LoginVM::class.java) -> {
                LoginVM(_userRepo) as T
            }
            modelClass.isAssignableFrom(StoryVM::class.java) -> {
                StoryVM(_userRepo, _storyRepo) as T
            }

            else -> throw IllegalArgumentException("Unknown Viewmodel Class: " + modelClass.name)
        }
    }

    companion object {
        private var INSTANCE: FactoryVM? = null
        fun getInstance(context: Context): FactoryVM {
            return INSTANCE ?: synchronized(this) {
                FactoryVM(
                    Injection.provideUserRepository(context.dataStore),
                    Injection.provideStoryRepository(context)).also {
                    INSTANCE = it
                }
            }
        }
    }

}