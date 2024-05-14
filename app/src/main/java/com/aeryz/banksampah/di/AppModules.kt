package com.aeryz.banksampah.di

import com.aeryz.banksampah.data.local.database.AppDatabase
import com.aeryz.banksampah.data.local.database.datasource.CartDataSource
import com.aeryz.banksampah.data.local.database.datasource.CartDatabaseDataSource
import com.aeryz.banksampah.data.local.datastore.UserPreferenceDataSource
import com.aeryz.banksampah.data.local.datastore.UserPreferenceDataSourceImpl
import com.aeryz.banksampah.data.local.datastore.appDataStore
import com.aeryz.banksampah.data.network.api.datasource.FoodGoApiDataSource
import com.aeryz.banksampah.data.network.api.datasource.FoodGoDataSource
import com.aeryz.banksampah.data.network.api.service.FoodGoApiService
import com.aeryz.banksampah.data.network.firebase.auth.FirebaseAuthDataSource
import com.aeryz.banksampah.data.network.firebase.auth.FirebaseAuthDataSourceImpl
import com.aeryz.banksampah.data.repository.CartRepository
import com.aeryz.banksampah.data.repository.CartRepositoryImpl
import com.aeryz.banksampah.data.repository.ProductRepository
import com.aeryz.banksampah.data.repository.ProductRepositoryImpl
import com.aeryz.banksampah.data.repository.UserRepository
import com.aeryz.banksampah.data.repository.UserRepositoryImpl
import com.aeryz.banksampah.presentation.cart.CartViewModel
import com.aeryz.banksampah.presentation.checkout.CheckoutViewModel
import com.aeryz.banksampah.presentation.detail.DetailViewModel
import com.aeryz.banksampah.presentation.home.HomeViewModel
import com.aeryz.banksampah.presentation.login.LoginViewModel
import com.aeryz.banksampah.presentation.main.MainViewModel
import com.aeryz.banksampah.presentation.profile.ProfileViewModel
import com.aeryz.banksampah.presentation.register.RegisterViewModel
import com.aeryz.banksampah.presentation.splashscreen.SplashViewModel
import com.aeryz.banksampah.settings.SettingsViewModel
import com.aeryz.banksampah.utils.PreferenceDataStoreHelper
import com.aeryz.banksampah.utils.PreferenceDataStoreHelperImpl
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.Module
import org.koin.dsl.module

object AppModules {

    fun getModules(): List<Module> = listOf(
        localModule,
        networkModule,
        dataSource,
        repository,
        viewModels
    )

    private val localModule = module {
        single { AppDatabase.getInstance(androidContext()) }
        single { get<AppDatabase>().cartDao() }
        single { androidContext().appDataStore }
        single<PreferenceDataStoreHelper> { PreferenceDataStoreHelperImpl(get()) }
        single<UserPreferenceDataSource> { UserPreferenceDataSourceImpl(get()) }
    }

    private val networkModule = module {
        single { ChuckerInterceptor(androidContext()) }
        single { FoodGoApiService.invoke(get()) }
        single { FirebaseAuth.getInstance() }
    }

    private val dataSource = module {
        single<FoodGoDataSource> { FoodGoApiDataSource(get()) }
        single<CartDataSource> { CartDatabaseDataSource(get()) }
        single<FirebaseAuthDataSource> { FirebaseAuthDataSourceImpl(get()) }
    }

    private val repository = module {
        single<ProductRepository> { ProductRepositoryImpl(get()) }
        single<CartRepository> { CartRepositoryImpl(get(), get()) }
        single<UserRepository> { UserRepositoryImpl(get()) }
    }

    private val viewModels = module {
        viewModel { HomeViewModel(get(), get()) }
        viewModel { params -> DetailViewModel(params.get(), get()) }
        viewModelOf(::MainViewModel)
        viewModelOf(::CartViewModel)
        viewModelOf(::CheckoutViewModel)
        viewModelOf(::ProfileViewModel)
        viewModelOf(::LoginViewModel)
        viewModelOf(::RegisterViewModel)
        viewModelOf(::SplashViewModel)
        viewModelOf(::SettingsViewModel)
    }
}