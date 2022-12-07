package com.example.storyappsubmission1.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.storyappsubmission1.Data.Repository.UserRepo
import com.example.storyappsubmission1.Data.Response.LoginResult
import kotlinx.coroutines.launch

class LoginVM(private val _userRepo: UserRepo) : ViewModel() {
    fun getToken(): LiveData<String> {
        return _userRepo.getToken().asLiveData()
    }

    fun login(email: String, password: String) = _userRepo.login(email, password)

    fun setUser(loginResult: LoginResult) {
        viewModelScope.launch {
            _userRepo.setUser(loginResult)
        }
    }
}