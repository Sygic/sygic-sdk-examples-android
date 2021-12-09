package com.sygic.sdk.example.activity

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sygic.sdk.SygicEngine
import com.sygic.sdk.context.CoreInitException
import com.sygic.sdk.context.SygicContext
import com.sygic.sdk.context.SygicContextInitRequest
import com.sygic.sdk.example.BuildConfig
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SdkActivityViewModel : ViewModel() {

    private val requestPermissionMutable = MutableLiveData<String>()
    val requestPermission: LiveData<String> = requestPermissionMutable

    fun init(applicationContext: Context) {
        viewModelScope.launch {
            initSygicSdk(applicationContext)
            requestPermissionMutable.postValue(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    fun onPermissionRequestResult(permission: String, result: Int) {
        if (permission == Manifest.permission.ACCESS_FINE_LOCATION && result == PackageManager.PERMISSION_GRANTED) {
            // start listening for GPS inside Sygic SDK
            SygicEngine.openGpsConnection()
        }
    }

    private fun getJsonConfigurationString(applicationContext: Context): String {
        val sdkPath = applicationContext.getExternalFilesDir(null).toString()

        val sdkConfigBuilder = SygicEngine.JsonConfigBuilder().apply {
            authentication(BuildConfig.SYGIC_SDK_CLIENT_ID)
            storageFolders().rootPath(sdkPath)
            mapReaderSettings().startupOnlineMapsEnabled(true)
        }

        return sdkConfigBuilder.build()
    }

    private suspend fun initSygicSdk(applicationContext: Context): SygicContext? {
        return suspendCoroutine { continuation ->
            val configJsonString = getJsonConfigurationString(applicationContext)
            val sdkInitRequest = SygicContextInitRequest(configJsonString, applicationContext)

            val sdkInitListener = object : SygicEngine.OnInitCallback {
                override fun onError(error: CoreInitException) {
                    Toast.makeText(applicationContext, error.message, Toast.LENGTH_LONG).show()
                    continuation.resume(null)
                }
                override fun onInstance(instance: SygicContext) {
                    continuation.resume(instance)
                }
            }
            SygicEngine.initialize(sdkInitRequest, sdkInitListener)
        }
    }
}