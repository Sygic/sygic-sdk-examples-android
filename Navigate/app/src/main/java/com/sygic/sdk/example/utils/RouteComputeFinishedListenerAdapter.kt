package com.sygic.sdk.example.utils

import com.sygic.sdk.route.AlternativeRouteResult
import com.sygic.sdk.route.Route
import com.sygic.sdk.route.listeners.RouteComputeFinishedListener

open class RouteComputeFinishedListenerAdapter : RouteComputeFinishedListener {
    override fun onComputeFinished(route: Route?, alternatives: List<AlternativeRouteResult>) {}
}