package com.aeryz.banksampah.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.aeryz.banksampah.data.repository.CartRepository
import com.aeryz.banksampah.model.Cart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CartViewModel(private val repo: CartRepository) : ViewModel() {

    val cartList = repo.getUserCartData().asLiveData(Dispatchers.IO)

    fun decreaseCart(item: Cart) {
        viewModelScope.launch { repo.decreaseCart(item).collect() }
    }

    fun increaseCart(item: Cart) {
        viewModelScope.launch { repo.increaseCart(item).collect() }
    }

    fun removeCart(item: Cart) {
        viewModelScope.launch { repo.deleteCart(item).collect() }
    }

    fun userDoneEditingNotes(item: Cart) {
        viewModelScope.launch { repo.setCartNotes(item).collect() }
    }
}
