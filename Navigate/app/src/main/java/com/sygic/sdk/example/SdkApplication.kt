package com.sygic.sdk.example

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class SdkApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}