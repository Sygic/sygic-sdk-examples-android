package com.sygic.sdk.example.activity

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sygic.sdk.example.databinding.ActivitySdkBinding

private const val PermissionRequestCode = 158

class SdkActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySdkBinding
    private val viewModel: SdkActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // init Sygic Maps SDK
        viewModel.requestPermission.observe(this, { checkForPermission(it) })
        viewModel.init(applicationContext)

        binding = ActivitySdkBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun checkForPermission(permission: String) {
        when (ContextCompat.checkSelfPermission(this, permission)) {
            PackageManager.PERMISSION_GRANTED -> {
                viewModel.onPermissionRequestResult(permission, PackageManager.PERMISSION_GRANTED)
            }
            else -> {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(permission),
                    PermissionRequestCode
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        viewModel.onPermissionRequestResult(permissions.first(), grantResults.first())
    }
}