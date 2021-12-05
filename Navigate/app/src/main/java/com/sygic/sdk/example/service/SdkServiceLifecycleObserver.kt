package com.sygic.sdk.example.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.core.content.ContextCompat

class SdkServiceLifecycleObserver(private val applicationContext: Context) {

    private var serviceBinder: SdkNavigationService.SdkServiceBinder? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            serviceBinder = service as SdkNavigationService.SdkServiceBinder
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            serviceBinder = null
        }
    }

    fun onStart() {
        startService()
    }

    fun onForeground() {
        serviceBinder?.stopForeground()
    }

    fun onBackground() {
        serviceBinder?.startForeground()
    }

    private fun startService() {
        val intent = Intent(applicationContext, SdkNavigationService::class.java)
        ContextCompat.startForegroundService(applicationContext, intent)
        applicationContext.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }
}