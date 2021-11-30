package com.sygic.sdk.example.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.sygic.sdk.example.databinding.ActivitySdkBinding

class SdkActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySdkBinding
    private val viewModel: SdkActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // init Sygic Maps SDK
        viewModel.init(applicationContext)

        binding = ActivitySdkBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}