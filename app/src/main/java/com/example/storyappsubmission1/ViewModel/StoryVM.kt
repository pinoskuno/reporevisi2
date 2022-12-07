package com.example.storyappsubmission1.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.storyappsubmission1.Data.Repository.StoryRepo
import com.example.storyappsubmission1.Data.Repository.UserRepo
import com.example.storyappsubmission1.Data.Response.LoginResult
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryVM(private val _userRepo: UserRepo,
              private val _storyRepo: StoryRepo
) : ViewModel() {

    fun getToken(): LiveData<String> {
        return _userRepo.getToken().asLiveData()
    }

    fun addStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: Float,
        lon: Float
    ) = _storyRepo.addStory(token, file, description, lat, lon)
}