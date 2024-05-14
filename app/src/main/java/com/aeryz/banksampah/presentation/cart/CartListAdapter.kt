package com.aeryz.banksampah.presentation.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.aeryz.banksampah.R
import com.aeryz.banksampah.core.ViewHolderBinder
import com.aeryz.banksampah.databinding.ItemCartProductBinding
import com.aeryz.banksampah.databinding.ItemCartProductOrderBinding
import com.aeryz.banksampah.model.Cart
import com.aeryz.banksampah.utils.doneEditing
import com.aeryz.banksampah.utils.toCurrencyFormat

class CartListAdapter(private val cartListener: CartListener? = null) :
    RecyclerView.Adapter<ViewHolder>() {

    private val dataDiffer =
        AsyncListDiffer(
            this,
            object : DiffUtil.ItemCallback<Cart>() {
                override fun areItemsTheSame(oldItem: Cart, newItem: Cart): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: Cart, newItem: Cart): Boolean {
                    return oldItem.hashCode() == newItem.hashCode()
                }
            }
        )

    fun submitData(data: List<Cart>) {
        dataDiffer.submitList(data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (cartListener != null) {
            CartViewHolder(
                ItemCartProductBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                cartListener
            )
        } else {
            CartOrderViewHolder(
                ItemCartProductOrderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return dataDiffer.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        (holder as ViewHolderBinder<Cart>).bind(dataDiffer.currentList[position])
    }
}

class CartOrderViewHolder(
    private val binding: ItemCartProductOrderBinding
) : ViewHolder(binding.root), ViewHolderBinder<Cart> {

    override fun bind(item: Cart) {
        setCartData(item)
        setCartNotes(item)
    }

    private fun setCartNotes(item: Cart) {
        binding.tvNotes.text = item.itemNotes
    }

    private fun setCartData(item: Cart) {
        with(binding) {
            sivProductImage.load(item.productImgUrl) {
                crossfade(true)
            }
            tvTotalQuantity.text = itemView.rootView.context.getString(
                R.string.total_quantity,
                item.itemQuantity.toString()
            )
            tvProductName.text = item.productName
            tvProductPrice.text = (item.itemQuantity * item.productPrice).toCurrencyFormat()
        }
    }
}

class CartViewHolder(
    private val binding: ItemCartProductBinding,
    private val cartListener: CartListener?
) : RecyclerView.ViewHolder(binding.root), ViewHolderBinder<Cart> {

    override fun bind(item: Cart) {
        setCartData(item)
        setCartNotes(item)
        setClickListener(item)
    }

    private fun setClickListener(item: Cart) {
        with(binding) {
            ivMinus.setOnClickListener { cartListener?.onMinusTotalItemCartClicked(item) }
            ivPlus.setOnClickListener { cartListener?.onPlusTotalItemCartClicked(item) }
            ivRemoveCart.setOnClickListener { cartListener?.onRemoveCartClicked(item) }
        }
    }

    private fun setCartNotes(item: Cart) {
        binding.etNotesItem.setText(item.itemNotes)
        binding.etNotesItem.doneEditing {
            binding.etNotesItem.clearFocus()
            val newItem = item.copy().apply {
                itemNotes = binding.etNotesItem.text.toString().trim()
            }
            cartListener?.onUserDoneEditingNotes(newItem)
        }
    }

    private fun setCartData(item: Cart) {
        with(binding) {
            sivProductImage.load(item.productImgUrl) {
                crossfade(true)
            }
            tvProductName.text = item.productName
            tvProductPrice.text = (item.itemQuantity * item.productPrice).toCurrencyFormat()
            tvProductCount.text = item.itemQuantity.toString()
        }
    }
}

interface CartListener {
    fun onPlusTotalItemCartClicked(cart: Cart)
    fun onMinusTotalItemCartClicked(cart: Cart)
    fun onRemoveCartClicked(cart: Cart)
    fun onUserDoneEditingNotes(cart: Cart)
}
