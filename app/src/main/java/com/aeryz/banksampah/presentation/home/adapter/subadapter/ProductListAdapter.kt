package com.aeryz.banksampah.presentation.home.adapter.subadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.aeryz.banksampah.core.ViewHolderBinder
import com.aeryz.banksampah.databinding.ItemGridFoodsBinding
import com.aeryz.banksampah.databinding.ItemLinearFoodsBinding
import com.aeryz.banksampah.model.Product
import com.aeryz.banksampah.presentation.home.adapter.viewholder.GridFoodItemViewHolder
import com.aeryz.banksampah.presentation.home.adapter.viewholder.LinearFoodItemViewHolder
import java.lang.IllegalArgumentException

class ProductListAdapter(
    var layoutMode: Int,
    private val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<ViewHolder>() {

    companion object {
        const val LINEAR_LAYOUT = 1
        const val GRID_LAYOUT = 2
    }

    private val dataDiffer = AsyncListDiffer(
        this,
        object : DiffUtil.ItemCallback<Product>() {
            override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }
        }
    )

    fun submitData(data: List<Product>) {
        dataDiffer.submitList(data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            LINEAR_LAYOUT -> {
                LinearFoodItemViewHolder(
                    binding = ItemLinearFoodsBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    onItemClick = onItemClick
                )
            }
            GRID_LAYOUT -> {
                GridFoodItemViewHolder(
                    binding = ItemGridFoodsBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    onItemClick = onItemClick
                )
            }
            else -> {
                throw IllegalArgumentException("Invalid View Type")
            }
        }
    }

    override fun getItemCount(): Int {
        return dataDiffer.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        (holder as ViewHolderBinder<Product>).bind(dataDiffer.currentList[position])
    }

    override fun getItemViewType(position: Int): Int {
        return layoutMode
    }

    fun refreshList() {
        notifyItemRangeChanged(0, dataDiffer.currentList.size)
    }
}
