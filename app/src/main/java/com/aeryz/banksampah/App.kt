package com.aeryz.banksampah

import android.app.Application
import com.aeryz.banksampah.di.AppModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
    private fun initKoin() {
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(AppModules.getModules())
        }
    }
}