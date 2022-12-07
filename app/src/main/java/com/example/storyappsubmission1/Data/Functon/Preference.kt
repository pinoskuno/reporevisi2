package com.example.storyappsubmission1.Data.Functon
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.storyappsubmission1.Data.Response.LoginResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class Preference private constructor(private val dataStore: DataStore<Preferences>){
    companion object {
        @Volatile
        private var INSTANCE: Preference? = null
        private val USERID_KEY = stringPreferencesKey("userId")
        private val NAME_KEY = stringPreferencesKey("name")
        private val TOKEN_KEY = stringPreferencesKey("token")

        fun getInstance(dataStore: DataStore<Preferences>): Preference {
            return INSTANCE ?: synchronized(this) {
                val instance = Preference(dataStore)
                INSTANCE = instance
                instance
            }
        }


    }

    fun getToken(): Flow<String> = dataStore.data.map { preferences ->
        preferences[TOKEN_KEY] ?: ""
    }

    suspend fun setUser(Result: LoginResult) {
        dataStore.edit { preferences ->
            preferences[USERID_KEY] = Result.userId
            preferences[NAME_KEY] = Result.name
            preferences[TOKEN_KEY] = Result.token
        }
    }

    suspend fun deleteUser() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }


}

