package com.sygic.sdk.example.fragment.data

import com.sygic.sdk.example.utils.Units
import com.sygic.sdk.route.RouteInfo

data class NavigationInfo(val firstLine: String, val secondLine: String) {

    companion object {
        fun fromRouteInfo(routeInfo: RouteInfo): NavigationInfo {
            val distance = Units.formatMeters(routeInfo.length)
            val duration = Units.formatDuration(routeInfo.waypointDurations.last().withSpeedProfileAndTraffic)
            val eta = Units.formatEstimatedTime(routeInfo.waypointDurations.last().withSpeedProfileAndTraffic)
            val secondLine = "$distance ・ $eta"
            return NavigationInfo(duration, secondLine)
        }
    }
}
