package com.aeryz.banksampah.presentation.detail

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import coil.load
import com.aeryz.banksampah.databinding.ActivityDetailBinding
import com.aeryz.banksampah.model.Product
import com.aeryz.banksampah.utils.proceedWhen
import com.aeryz.banksampah.utils.toCurrencyFormat
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class DetailActivity : AppCompatActivity() {

    private val binding: ActivityDetailBinding by lazy {
        ActivityDetailBinding.inflate(layoutInflater)
    }

    private val viewModel: DetailViewModel by viewModel {
        parametersOf(intent.extras ?: bundleOf())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        bindProduct(viewModel.product)
        observeData()
        setClickListener(viewModel.product)
    }

    private fun setClickListener(product: Product?) {
        binding.cvBack.setOnClickListener {
            onBackPressed()
        }
        binding.ivMinusButton.setOnClickListener {
            viewModel.minus()
        }
        binding.ivPlusButton.setOnClickListener {
            viewModel.add()
        }

        binding.bAddToCart.setOnClickListener {
            val itemNotes = binding.etNotes.text.toString()
            viewModel.addToCart(itemNotes)
        }
    }

    private fun observeData() {
        viewModel.priceLiveData.observe(this) {
            binding.tvFoodPriceLiveData.text = it.toCurrencyFormat()
        }
        viewModel.productCountLiveData.observe(this) {
            binding.tvFoodTotal.text = it.toString()
        }
        viewModel.addToCartResult.observe(this) {
            it.proceedWhen(
                doOnSuccess = {
                    Toast.makeText(this, "Add to cart success!", Toast.LENGTH_SHORT).show()
                    finish()
                },
                doOnError = {
                    Toast.makeText(this, it.exception?.message.orEmpty(), Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun bindProduct(product: Product?) {
        product?.let { item ->
            binding.ivFoodDetailImg.load(item.productImageUrl) {
                crossfade(true)
            }
            binding.tvFoodName.text = item.productName
            binding.tvFoodPrice.text = item.productPrice.toCurrencyFormat()
            binding.tvFoodPriceLiveData.text = item.productPrice.toCurrencyFormat()
            binding.tvFoodDescription.text = item.productDetail
        }
    }

    companion object {
        const val EXTRA_PRODUCT = "EXTRA_PRODUCT"

        fun startActivity(context: Context, product: Product) {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(EXTRA_PRODUCT, product)
            context.startActivity(intent)
        }
    }
}
