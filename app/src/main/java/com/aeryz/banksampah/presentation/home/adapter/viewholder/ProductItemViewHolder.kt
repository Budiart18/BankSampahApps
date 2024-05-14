package com.aeryz.banksampah.presentation.home.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.aeryz.banksampah.core.ViewHolderBinder
import com.aeryz.banksampah.databinding.ItemGridFoodsBinding
import com.aeryz.banksampah.databinding.ItemLinearFoodsBinding
import com.aeryz.banksampah.model.Product
import com.aeryz.banksampah.utils.toCurrencyFormat

class LinearFoodItemViewHolder(
    private val binding: ItemLinearFoodsBinding,
    private val onItemClick: (Product) -> Unit
) : RecyclerView.ViewHolder(binding.root), ViewHolderBinder<Product> {
    override fun bind(item: Product) {
        binding.root.setOnClickListener {
            onItemClick.invoke(item)
        }
        binding.sivFoodImage.load(item.productImageUrl) {
            crossfade(true)
        }
        binding.tvFoodName.text = item.productName
        binding.tvFoodPrice.text = item.productFormattedPrice
    }
}

class GridFoodItemViewHolder(
    private val binding: ItemGridFoodsBinding,
    private val onItemClick: (Product) -> Unit
) : RecyclerView.ViewHolder(binding.root), ViewHolderBinder<Product> {
    override fun bind(item: Product) {
        binding.root.setOnClickListener {
            onItemClick.invoke(item)
        }
        binding.sivFoodImage.load(item.productImageUrl) {
            crossfade(true)
        }
        binding.tvFoodName.text = item.productName
        binding.tvFoodPrice.text = item.productFormattedPrice
    }
}
