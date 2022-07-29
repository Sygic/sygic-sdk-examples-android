package com.sygic.sdk.example.common.ktx

import com.sygic.sdk.context.CoreInitCallback
import com.sygic.sdk.context.CoreInitException
import com.sygic.sdk.position.GeoCoordinates
import com.sygic.sdk.search.ReverseGeocoder
import com.sygic.sdk.search.ReverseGeocoderProvider
import com.sygic.sdk.search.ReverseGeocodingResult
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class SdkReverseGeocoder {
    private suspend fun get(): ReverseGeocoder {
        return suspendCoroutine {
            ReverseGeocoderProvider.getInstance(object : CoreInitCallback<ReverseGeocoder> {
                override fun onError(error: CoreInitException) {
                    it.resumeWithException(Throwable("Unable to get ReverseGeocoder"))
                }

                override fun onInstance(instance: ReverseGeocoder) {
                    it.resume(instance)
                }
            })
        }
    }

    suspend fun reverseGeocode(geoCoordinates: GeoCoordinates): List<ReverseGeocodingResult> {
        val reverseGeocoder = get()
        return suspendCoroutine {
            reverseGeocoder.reverseGeocode(
                geoCoordinates,
                emptySet(),
                object : ReverseGeocoder.ReverseGeocodingResultListener {
                    override fun onReverseGeocodingResult(result: List<ReverseGeocodingResult>) {
                        it.resume(result)
                    }

                    override fun onReverseGeocodingResultError(code: ReverseGeocoder.ErrorCode) {
                        it.resume(emptyList())
                    }
                })
        }
    }
}
