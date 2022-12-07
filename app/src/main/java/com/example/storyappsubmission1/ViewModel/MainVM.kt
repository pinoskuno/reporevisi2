package com.example.storyappsubmission1.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyappsubmission1.Data.Repository.StoryRepo
import com.example.storyappsubmission1.Data.Repository.UserRepo
import com.example.storyappsubmission1.Data.Response.StoryModel
import kotlinx.coroutines.launch

class MainVM (
    private val _userRepo : UserRepo,
    private val _storyRepo : StoryRepo ) : ViewModel() {
    fun getToken(): LiveData<String> {
        return _userRepo.getToken().asLiveData()
    }

    fun getStories(token: String): LiveData<PagingData<StoryModel>> =
        _storyRepo.getStories(token).cachedIn(viewModelScope)

    fun logout() = viewModelScope.launch {
        _userRepo.deleteUser()
    }


}