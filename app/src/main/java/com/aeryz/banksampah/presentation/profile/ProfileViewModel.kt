package com.aeryz.banksampah.presentation.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.aeryz.banksampah.data.local.datastore.UserPreferenceDataSource
import com.aeryz.banksampah.data.repository.UserRepository
import com.aeryz.banksampah.utils.ResultWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: UserRepository,
    private val userPreferenceDataSource: UserPreferenceDataSource
) : ViewModel() {

    private val _changePhotoResult = MutableLiveData<ResultWrapper<Boolean>>()
    val changePhotoResult: LiveData<ResultWrapper<Boolean>>
        get() = _changePhotoResult

    private val _changeProfileResult = MutableLiveData<ResultWrapper<Boolean>>()
    val changeProfileResult: LiveData<ResultWrapper<Boolean>>
        get() = _changeProfileResult

    val userAddress = userPreferenceDataSource.getUserAddressPrefFlow().asLiveData(Dispatchers.IO)

    fun setUserAddress(address: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userPreferenceDataSource.setUserAddressPref(address)
        }
    }

    fun getCurrentUser() = repository.getCurrentUser()

    fun updateProfilePicture(photoUri: Uri?) {
        viewModelScope.launch(Dispatchers.IO) {
            photoUri?.let {
                repository.updateProfile(photoUri = photoUri).collect {
                    _changePhotoResult.postValue(it)
                }
            }
        }
    }

    fun updateFullName(fullName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateProfile(fullName).collect {
                _changeProfileResult.postValue(it)
            }
        }
    }

    fun createChangePwdRequest() {
        repository.sendChangePasswordRequestByEmail()
    }

    fun doLogout() {
        repository.doLogout()
    }

    private val _isEditModeEnabled = MutableLiveData<Boolean>()
    val isEditModeEnabled: LiveData<Boolean>
        get() = _isEditModeEnabled
    init {
        _isEditModeEnabled.value = false
    }

    fun toggleEditMode() {
        _isEditModeEnabled.value = _isEditModeEnabled.value?.not()
    }
}
