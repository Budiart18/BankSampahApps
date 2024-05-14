package com.aeryz.banksampah.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import coil.load
import com.aeryz.banksampah.R
import com.aeryz.banksampah.databinding.FragmentHomeBinding
import com.aeryz.banksampah.model.Product
import com.aeryz.banksampah.presentation.detail.DetailActivity
import com.aeryz.banksampah.presentation.home.adapter.subadapter.CategoryListAdapter
import com.aeryz.banksampah.presentation.home.adapter.subadapter.ProductListAdapter
import com.aeryz.banksampah.presentation.home.adapter.subadapter.ProductListAdapter.Companion.GRID_LAYOUT
import com.aeryz.banksampah.presentation.home.adapter.subadapter.ProductListAdapter.Companion.LINEAR_LAYOUT
import com.aeryz.banksampah.presentation.settings.SettingsDialogFragment
import com.aeryz.banksampah.utils.proceedWhen
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private val categoryAdapter: CategoryListAdapter by lazy {
        CategoryListAdapter() {
            viewModel.getProducts(it.categoryName.lowercase())
        }
    }
    private val productAdapter: ProductListAdapter by lazy {
        ProductListAdapter(LINEAR_LAYOUT) {
            navigateToDetail(it)
        }
    }

    private val viewModel: HomeViewModel by viewModel()

    private fun navigateToDetail(product: Product) {
        DetailActivity.startActivity(requireContext(), product)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListener()
        getData()
        setUpProductRv()
        observeCategoryData()
        observeUser()
    }

    private fun observeUser() {
        viewModel.userAddress.observe(viewLifecycleOwner){ address ->
            binding.tvUserLocation.text = address
        }
    }

    private fun getData() {
        viewModel.getCategories()
        viewModel.getProducts()
    }

    private fun observeCategoryData() {
        viewModel.categories.observe(viewLifecycleOwner) {
            it.proceedWhen(
                doOnSuccess = {
                    binding.layoutStateCategory.root.isVisible = false
                    binding.layoutStateCategory.pbLoading.isVisible = false
                    binding.layoutStateCategory.tvError.isVisible = false
                    binding.rvListCategories.apply {
                        isVisible = true
                        adapter = categoryAdapter
                    }
                    it.payload?.let { data -> categoryAdapter.submitData(data) }
                },
                doOnLoading = {
                    binding.layoutStateCategory.root.isVisible = true
                    binding.layoutStateCategory.pbLoading.isVisible = true
                    binding.layoutStateCategory.tvError.isVisible = false
                    binding.rvListCategories.isVisible = false
                },
                doOnError = {
                    binding.layoutStateCategory.root.isVisible = true
                    binding.layoutStateCategory.pbLoading.isVisible = false
                    binding.layoutStateCategory.tvError.isVisible = true
                    binding.layoutStateCategory.tvError.text = it.exception?.message.orEmpty()
                    binding.rvListCategories.isVisible = false
                }
            )
        }
    }

    private fun observeProductData() {
        viewModel.products.observe(viewLifecycleOwner) {
            it.proceedWhen(
                doOnSuccess = {
                    it.payload?.let { data -> productAdapter.submitData(data) }
                    binding.layoutStateProduct.root.isVisible = false
                    binding.layoutStateProduct.pbLoading.isVisible = false
                    binding.rvListFoods.isVisible = true
                    binding.layoutStateProduct.tvError.isVisible = false
                },
                doOnLoading = {
                    binding.layoutStateProduct.root.isVisible = true
                    binding.layoutStateProduct.pbLoading.isVisible = true
                    binding.layoutStateProduct.tvError.isVisible = false
                    binding.rvListFoods.isVisible = false
                },
                doOnError = {
                    binding.layoutStateProduct.root.isVisible = true
                    binding.layoutStateProduct.pbLoading.isVisible = false
                    binding.layoutStateProduct.tvError.isVisible = true
                    binding.layoutStateProduct.tvError.text = it.exception?.message.orEmpty()
                    binding.rvListFoods.isVisible = false
                },
                doOnEmpty = {
                    binding.layoutStateProduct.root.isVisible = true
                    binding.layoutStateProduct.pbLoading.isVisible = false
                    binding.layoutStateProduct.tvError.isVisible = true
                    binding.layoutStateProduct.tvError.text = R.string.text_product_not_found.toString()
                    binding.rvListFoods.isVisible = false
                }
            )
        }
    }

    private fun setUpProductRv() {
        viewModel.userLayoutMode.observe(viewLifecycleOwner) { layoutMode ->
            binding.rvListFoods.apply {
                adapter = productAdapter
                layoutManager = GridLayoutManager(requireContext(), layoutMode)
            }
            productAdapter.layoutMode = layoutMode
            observeProductData()
        }
    }

    private fun setClickListener() {
        binding.ivSettings.setOnClickListener {
            openSettingDialog()
        }
        binding.ibSwitchMode.setOnClickListener {
            changeAdapterLayoutMode()
        }
    }

    private fun changeAdapterLayoutMode() {
        if (productAdapter.layoutMode == LINEAR_LAYOUT) {
            productAdapter.layoutMode = GRID_LAYOUT
            binding.ibSwitchMode.load(R.drawable.ic_grid_mode)
            viewModel.setUserLayoutMode(GRID_LAYOUT)
            (binding.rvListFoods.layoutManager as GridLayoutManager).spanCount = 2
        } else if (productAdapter.layoutMode == GRID_LAYOUT) {
            productAdapter.layoutMode = LINEAR_LAYOUT
            binding.ibSwitchMode.load(R.drawable.ic_linear_mode)
            viewModel.setUserLayoutMode(LINEAR_LAYOUT)
            (binding.rvListFoods.layoutManager as GridLayoutManager).spanCount = 1
        }
        productAdapter.refreshList()
    }

    private fun openSettingDialog() {
        SettingsDialogFragment().show(childFragmentManager, null)
    }
}
