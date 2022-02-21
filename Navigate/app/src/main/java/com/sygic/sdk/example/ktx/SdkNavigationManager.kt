package com.sygic.sdk.example.ktx

import com.sygic.sdk.context.CoreInitCallback
import com.sygic.sdk.context.CoreInitException
import com.sygic.sdk.navigation.NavigationManager
import com.sygic.sdk.navigation.NavigationManagerProvider
import com.sygic.sdk.navigation.routeeventnotifications.DirectionInfo
import com.sygic.sdk.navigation.routeeventnotifications.LaneInfo
import com.sygic.sdk.route.Route
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class SdkNavigationManager {

    private suspend fun get(): NavigationManager {
        return suspendCoroutine {
            NavigationManagerProvider.getInstance(object : CoreInitCallback<NavigationManager> {
                override fun onError(error: CoreInitException) {
                    it.resumeWithException(Throwable("Unable to get NavigationManager"))
                }

                override fun onInstance(instance: NavigationManager) {
                    it.resume(instance)
                }
            })
        }
    }

    fun routeChanged(): Flow<Route?> = callbackFlow {
        val manager = get()
        val listener = NavigationManager.OnRouteChangedListener { route, _ ->
            launch { send(route) }
        }

        manager.addOnRouteChangedListener(listener)
        awaitClose { manager.removeOnRouteChangedListener(listener) }
    }

    fun directions(): Flow<DirectionInfo> = callbackFlow {
        val manager = get()
        val listener = NavigationManager.OnDirectionListener {
            launch { send(it) }
        }
        manager.addOnDirectionListener(listener)
        awaitClose { manager.removeOnDirectionListener(listener) }
    }

    fun lanes(): Flow<LaneInfo> = callbackFlow {
        val manager = get()
        val listener= NavigationManager.OnLaneListener {
            launch { send(it) }
        }
        manager.addOnLaneListener(listener)
        awaitClose { manager.removeOnLaneListener (listener) }
    }

    suspend fun currentRoute(): Route? = get().currentRoute
    suspend fun setRouteForNavigation(route: Route) = get().setRouteForNavigation(route)
    suspend fun stopNavigation() = get().stopNavigation()
}