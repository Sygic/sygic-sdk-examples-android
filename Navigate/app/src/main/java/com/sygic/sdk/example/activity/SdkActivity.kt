package com.sygic.sdk.example.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.sygic.sdk.example.databinding.ActivitySdkBinding
import com.sygic.sdk.example.service.SdkServiceLifecycleObserver

class SdkActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySdkBinding
    private lateinit var sdkServiceLifecycleObserver: SdkServiceLifecycleObserver
    private val viewModel: SdkActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sdkServiceLifecycleObserver = SdkServiceLifecycleObserver(applicationContext)

        viewModel.init(applicationContext)

        binding = ActivitySdkBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()

        // start foreground service to be able to receive location updates when app is on background
        sdkServiceLifecycleObserver.onStart()
    }

    override fun onResume() {
        super.onResume()

        // app is in foreground, stop service and hide service notification
        sdkServiceLifecycleObserver.onForeground()
    }

    override fun onPause() {
        super.onPause()

        // app is going to background, start service and show service notification
        sdkServiceLifecycleObserver.onBackground()
    }
}