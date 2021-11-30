package com.sygic.sdk.example.ktx

import com.sygic.sdk.context.CoreInitCallback
import com.sygic.sdk.context.CoreInitException
import com.sygic.sdk.position.GeoCourse
import com.sygic.sdk.position.GeoPosition
import com.sygic.sdk.position.PositionManager
import com.sygic.sdk.position.PositionManagerProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class SdkPositionManager {
    private suspend fun get(): PositionManager {
        return suspendCoroutine {
            PositionManagerProvider.getInstance(object : CoreInitCallback<PositionManager> {
                override fun onError(error: CoreInitException) {
                    it.resumeWithException(Throwable("Unable to get PositionManager"))
                }

                override fun onInstance(instance: PositionManager) {
                    it.resume(instance)
                }
            })
        }
    }

    fun positions(): Flow<GeoPosition?> = callbackFlow {
        val positionManager = get()
        val listener = object : PositionManager.PositionChangeListener {
            override fun onCourseChanged(geoCourse: GeoCourse) {
            }

            override fun onPositionChanged(geoPosition: GeoPosition) {
                launch { send(geoPosition) }
            }
        }

        positionManager.addPositionChangeListener(listener)
        awaitClose { positionManager.removePositionChangeListener(listener) }
    }

    suspend fun lastKnownPosition() = get().lastKnownPosition
    suspend fun position() = positions().firstOrNull() ?: GeoPosition.Invalid
}