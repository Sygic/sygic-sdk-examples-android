package com.sygic.sdk.example.fragment.data

import com.sygic.sdk.example.utils.Units
import com.sygic.sdk.route.RouteInfo

data class NavigationInfo(val distance: String, val time: String) {

    companion object {
        fun fromRouteInfo(routeInfo: RouteInfo): NavigationInfo {
            val distance = Units.formatMeters(routeInfo.length)
            val time = Units.formatSecondsToClock(routeInfo.waypointDurations.last().withSpeedProfileAndTraffic)
            return NavigationInfo(distance, time)
        }
    }
}
