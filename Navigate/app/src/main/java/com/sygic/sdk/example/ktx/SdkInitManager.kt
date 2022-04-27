package com.sygic.sdk.example.ktx

import android.content.Context
import android.widget.Toast
import com.sygic.sdk.SygicEngine
import com.sygic.sdk.context.CoreInitException
import com.sygic.sdk.context.SygicContext
import com.sygic.sdk.context.SygicContextInitRequest
import com.sygic.sdk.example.BuildConfig
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SdkInitManager @Inject constructor(
    private val applicationContext: Context
) {
    suspend fun initSdk(): SygicContext? {
        return suspendCoroutine { continuation ->
            val configJsonString = getJsonConfigurationString()
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

    private fun getJsonConfigurationString(): String {
        val sdkPath = applicationContext.getExternalFilesDir(null).toString()

        val sdkConfigBuilder = SygicEngine.JsonConfigBuilder().apply {
            authentication(BuildConfig.SYGIC_SDK_CLIENT_ID)
            storageFolders().rootPath(sdkPath)
            mapReaderSettings().startupOnlineMapsEnabled(true)
        }

        return sdkConfigBuilder.build()
    }
}