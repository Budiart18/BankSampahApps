package com.aeryz.banksampah.presentation.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aeryz.banksampah.data.repository.UserRepository
import com.aeryz.banksampah.utils.ResultWrapper
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<ResultWrapper<Boolean>>()

    val loginResult: LiveData<ResultWrapper<Boolean>>
        get() = _loginResult

    fun doLogin(email: String, password: String) {
        viewModelScope.launch {
            repository.doLogin(email, password).collect { result ->
                _loginResult.postValue(result)
            }
        }
    }
}
