package com.aeryz.banksampah.presentation.detail

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aeryz.banksampah.data.repository.CartRepository
import com.aeryz.banksampah.model.Product
import com.aeryz.banksampah.utils.ResultWrapper
import kotlinx.coroutines.launch

class DetailViewModel(
    private val extras: Bundle?,
    private val cartRepository: CartRepository
) : ViewModel() {

    val product = extras?.getParcelable<Product>(DetailActivity.EXTRA_PRODUCT)

    val priceLiveData = MutableLiveData<Double>()

    val productCountLiveData = MutableLiveData<Int>().apply {
        postValue(1)
    }

    private val _addToCartResult = MutableLiveData<ResultWrapper<Boolean>>()

    val addToCartResult: LiveData<ResultWrapper<Boolean>>
        get() = _addToCartResult

    fun addToCart(itemNotes: String) {
        viewModelScope.launch {
            val productQuantity =
                if ((productCountLiveData.value ?: 0) <= 0) 1 else productCountLiveData.value ?: 0
            product?.let {
                cartRepository.createCart(it, productQuantity, itemNotes).collect { result ->
                    _addToCartResult.postValue(result)
                }
            }
        }
    }

    fun add() {
        val count = (productCountLiveData.value ?: 0) + 1
        productCountLiveData.postValue(count)
        priceLiveData.postValue(product?.productPrice?.times(count) ?: 0.0)
    }

    fun minus() {
        if ((productCountLiveData.value ?: 0) > 1) {
            val count = (productCountLiveData.value ?: 0) - 1
            productCountLiveData.postValue(count)
            priceLiveData.postValue(product?.productPrice?.times(count) ?: 0.0)
        }
    }
}
