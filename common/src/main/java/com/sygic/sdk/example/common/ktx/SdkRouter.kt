package com.sygic.sdk.example.common.ktx

import com.sygic.sdk.context.CoreInitCallback
import com.sygic.sdk.context.CoreInitException
import com.sygic.sdk.example.common.utils.RouteComputeListenerAdapter
import com.sygic.sdk.route.*
import com.sygic.sdk.route.listeners.RouteComputeFinishedListener
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class SdkRouter {

    private suspend fun get(): Router {
        return suspendCoroutine {
            RouterProvider.getInstance(object : CoreInitCallback<Router> {
                override fun onError(error: CoreInitException) {
                    it.resumeWithException(Throwable("Unable to get Router"))
                }

                override fun onInstance(instance: Router) {
                    it.resume(instance)
                }
            })
        }
    }

    suspend fun computeRoute(routeRequest: RouteRequest): Route? {
        val router = get()
        return suspendCoroutine {
            val primaryRouteRequest = PrimaryRouteRequest(routeRequest, RouteComputeListenerAdapter())
            val computeListener = object : RouteComputeFinishedListener {
                override fun onComputeFinished(
                    route: Route?,
                    alternatives: List<AlternativeRouteResult>
                ) {
                    it.resume(route)
                }
            }
            router.computeRouteWithAlternatives(primaryRouteRequest, listener = computeListener)
        }
    }
}