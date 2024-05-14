package com.aeryz.banksampah.presentation.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aeryz.banksampah.data.repository.UserRepository
import com.aeryz.banksampah.utils.ResultWrapper
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: UserRepository) : ViewModel() {

    private val _registerResult = MutableLiveData<ResultWrapper<Boolean>>()

    val registerResult: LiveData<ResultWrapper<Boolean>>
        get() = _registerResult

    fun doRegister(fullName: String, email: String, password: String) {
        viewModelScope.launch {
            repository.doRegister(fullName, email, password).collect() { result ->
                _registerResult.postValue(result)
            }
        }
    }
}
