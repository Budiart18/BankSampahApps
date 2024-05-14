package com.aeryz.banksampah.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.aeryz.banksampah.data.local.datastore.UserPreferenceDataSource
import com.aeryz.banksampah.data.repository.ProductRepository
import com.aeryz.banksampah.model.Category
import com.aeryz.banksampah.model.Product
import com.aeryz.banksampah.utils.ResultWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: ProductRepository,
    private val userPreferenceDataSource: UserPreferenceDataSource
) : ViewModel() {

    private val _categories = MutableLiveData<ResultWrapper<List<Category>>>()
    val categories: LiveData<ResultWrapper<List<Category>>>
        get() = _categories

    private val _products = MutableLiveData<ResultWrapper<List<Product>>>()
    val products: LiveData<ResultWrapper<List<Product>>>
        get() = _products

    val userLayoutMode = userPreferenceDataSource.getUserLayoutModePrefFlow().asLiveData(Dispatchers.IO)

    val userAddress = userPreferenceDataSource.getUserAddressPrefFlow().asLiveData(Dispatchers.IO)

    fun getCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getCategories().collect {
                _categories.postValue(it)
            }
        }
    }

    fun getProducts(category: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getProducts(category).collect {
                _products.postValue(it)
            }
        }
    }

    fun setUserLayoutMode(layoutMode: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            userPreferenceDataSource.setUserLayoutModePref(layoutMode)
        }
    }

}
