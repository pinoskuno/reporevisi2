package com.example.storyappsubmission1.Data.Repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.*
import com.example.storyappsubmission1.Data.API.Service
import com.example.storyappsubmission1.Data.Functon.StoryDatabase
import com.example.storyappsubmission1.Data.Functon.StoryRemoteMediator
import com.example.storyappsubmission1.Data.Functon.wrapEspressoIdlingResource
import com.example.storyappsubmission1.Data.Response.GeneralR
import com.example.storyappsubmission1.Data.Response.StoryModel
import com.example.storyappsubmission1.Data.Response.StoryR
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.lang.Exception
import com.example.storyappsubmission1.Data.Functon.Result

class StoryRepo(
    private val storyDatabase: StoryDatabase,
    private val _service: Service
) {

    @OptIn(ExperimentalPagingApi::class)
    fun getStories(token: String): LiveData<PagingData<StoryModel>> {
        wrapEspressoIdlingResource {
            return Pager(
                config = PagingConfig(
                    pageSize = 10
                ),
                remoteMediator = StoryRemoteMediator(storyDatabase, _service, token),
                pagingSourceFactory = {
                    storyDatabase.storyDao().getAll()
                }
            ).liveData
        }
    }

    fun getStoriesWithLocation(token: String): LiveData<Result<StoryR>> = liveData {
        wrapEspressoIdlingResource {
            emit(Result.Loading())
            try {
                val client = _service.getStories("Bearer $token", size = 20, location = 1)
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

    fun getStory(token: String, id: String): LiveData<Result<StoryR>> = liveData {
        wrapEspressoIdlingResource {
            emit(Result.Loading())
            try {
                val client = _service.getStory("Bearer $token", id)
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

    fun addStory(
        token: String,
        imageFile: MultipartBody.Part,
        description: RequestBody,
        latitude: Float,
        longitude: Float
    ): LiveData<Result<GeneralR>> = liveData {
        wrapEspressoIdlingResource {
            emit(Result.Loading())
            try {
                val client =
                    _service.addStory(
                        "Bearer $token",
                        imageFile,
                        description,
                        latitude,
                        longitude
                    )
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

    companion object {
        private const val TAG = "StoryRepository"
        private var INSTANCE: StoryRepo? = null
        fun getInstance(storyDatabase: StoryDatabase, _service: Service): StoryRepo {
            return INSTANCE ?: synchronized(this) {
                StoryRepo(storyDatabase, _service).also {
                    INSTANCE = it
                }
            }
        }
    }
}