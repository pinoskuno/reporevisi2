package com.example.storyappsubmission1.ViewModel

import androidx.lifecycle.ViewModel
import com.example.storyappsubmission1.Data.Repository.UserRepo

class RegisterVM(private val _userRepo: UserRepo) : ViewModel(){
    fun register(name: String, email: String, password: String) =
        _userRepo.register(name, email, password)
}