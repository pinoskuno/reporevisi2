package com.example.storyappsubmission1.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.storyappsubmission1.Data.Repository.StoryRepo
import com.example.storyappsubmission1.Data.Repository.UserRepo
import kotlinx.coroutines.launch

class MapsVM (private val _userRepo: UserRepo,
private val _storyRepo: StoryRepo) : ViewModel(){
    fun getToken(): LiveData<String> {
        return _userRepo.getToken().asLiveData()
    }

    fun getStoriesWithLocation(token: String) = _storyRepo.getStoriesWithLocation(token)

}