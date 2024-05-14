package com.aeryz.banksampah.presentation.splashscreen

import androidx.lifecycle.ViewModel
import com.aeryz.banksampah.data.repository.UserRepository

class SplashViewModel(private val repository: UserRepository) : ViewModel() {

    fun isUserLoggedIn() = repository.isLoggedIn()
}
