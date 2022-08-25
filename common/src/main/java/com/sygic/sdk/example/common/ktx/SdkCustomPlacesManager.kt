package com.sygic.sdk.example.common.ktx

import com.sygic.sdk.context.CoreInitCallback
import com.sygic.sdk.context.CoreInitException
import com.sygic.sdk.places.CustomPlacesManager
import com.sygic.sdk.places.CustomPlacesManagerProvider
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class SdkCustomPlacesManager {
    private suspend fun get(): CustomPlacesManager {
        return suspendCoroutine {
            CustomPlacesManagerProvider.getInstance(object : CoreInitCallback<CustomPlacesManager> {
                override fun onError(error: CoreInitException) {
                    it.resumeWithException(Throwable("Unable to get ReverseGeocoder"))
                }

                override fun onInstance(instance: CustomPlacesManager) {
                    it.resume(instance)
                }
            })
        }
    }

    suspend fun installOfflinePlacesFromJson(jsonStream: String) {
        val customPlacesManager = get()
        suspendCoroutine {
            customPlacesManager.installOfflinePlacesFromJson(
                jsonStream,
                object : CustomPlacesManager.InstallResultListener {
                    override fun onResult(
                        result: CustomPlacesManager.InstallResult,
                        message: String
                    ) {
                        when (result) {
                            CustomPlacesManager.InstallResult.SUCCESS -> {
                                it.resume(Unit)
                            }
                            CustomPlacesManager.InstallResult.FAIL -> {
                                it.resumeWithException(RuntimeException("Installation of custom places failed ($result, $message)"))
                            }
                            CustomPlacesManager.InstallResult.CANCELED -> {
                                it.resumeWithException(RuntimeException("Installation of custom places cancelled ($result, $message)"))
                            }
                        }
                    }
                })
        }
    }

    suspend fun setMode(mode: CustomPlacesManager.Mode) {
        val customPlacesManager = get()
        customPlacesManager.setMode(mode)
    }
}
