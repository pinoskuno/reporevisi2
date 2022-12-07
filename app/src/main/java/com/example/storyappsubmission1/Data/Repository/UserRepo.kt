package com.example.storyappsubmission1.Data.Repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.storyappsubmission1.Data.API.Service
import com.example.storyappsubmission1.Data.Functon.Preference
import com.example.storyappsubmission1.Data.Functon.wrapEspressoIdlingResource
import com.example.storyappsubmission1.Data.Response.GeneralR
import com.example.storyappsubmission1.Data.Response.LoginR
import com.example.storyappsubmission1.Data.Response.LoginResult
import com.example.storyappsubmission1.Data.Functon.Result

class UserRepo(
    private val _prefrence: Preference,
    private val _service: Service
) {

    fun register(name: String, email: String, password: String): LiveData<Result<GeneralR>> =
        liveData {
            wrapEspressoIdlingResource {
                emit(Result.Loading())
                try {
                    val client = _service.register(name, email, password)
                    if (!client.error) {
                        emit(Result.Success(client))
                    } else {
                        emit(Result.Error(client.message))
                        Log.e(TAG, client.message)
                    }
                } catch (e: Exception) {
                    emit(Result.Error(e.message.toString()))
                    Log.e(TAG, e.message.toString())
                }
            }
        }

    fun login(email: String, password: String): LiveData<Result<LoginR>> = liveData {
        wrapEspressoIdlingResource {
            emit(Result.Loading())
            try {
                val client = _service.login(email, password)
                if (!client.error) {
                    emit(Result.Success(client))
                } else {
                    emit(Result.Error(client.message))
                    Log.e(TAG, client.message)
                }
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
                Log.e(TAG, e.message.toString())
            }
        }
    }

    suspend fun setUser(loginResult: LoginResult) = _prefrence.setUser(loginResult)

    fun getToken() = _prefrence.getToken()

    suspend fun deleteUser() = _prefrence.deleteUser()

    companion object {
        private const val TAG = "UserRepository"
        private var INSTANCE: UserRepo? = null
        fun getInstance(_prefrence: Preference, _service: Service): UserRepo {
            return INSTANCE ?: synchronized(this) {
                UserRepo(_prefrence, _service).also {Preference
                    INSTANCE = it
                }
            }
        }
    }

}

