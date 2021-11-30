package com.sygic.sdk.example.utils

import com.sygic.sdk.route.Route
import com.sygic.sdk.route.Router
import com.sygic.sdk.route.listeners.RouteComputeListener

open class RouteComputeListenerAdapter : RouteComputeListener {
    override fun onComputeFinished(route: Route?, status: Router.RouteComputeStatus) {}
    override fun onProgress(progress: Int) {}
}
