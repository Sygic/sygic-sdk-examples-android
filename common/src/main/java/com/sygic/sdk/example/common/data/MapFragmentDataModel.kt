package com.sygic.sdk.example.common.data

import com.sygic.sdk.map.`object`.MapMarker
import com.sygic.sdk.map.`object`.MapRoute
import com.sygic.sdk.map.data.SimpleMapDataModel
import com.sygic.sdk.position.GeoCoordinates
import com.sygic.sdk.route.Route

class MapFragmentDataModel : SimpleMapDataModel() {

    private var mapResultMarker: MapMarker? = null

    private var mapRoute: MapRoute? = null
    private var destinationMarker: MapMarker? = null
    private var startMarker: MapMarker? = null

    fun clearData() {
        clearMapResultMarker()
        clearMapRoute()
    }

    fun setMapResultMarker(geoCoordinates: GeoCoordinates) {
        clearMapResultMarker()
        mapResultMarker = MapMarker.at(geoCoordinates).build().apply {
            addMapObject(this)
        }
    }

    fun clearMapResultMarker() {
        mapResultMarker?.let {
            removeMapObject(it)
        }
        mapResultMarker = null
    }

    fun setMapRoute(route: Route, createStartMarker: Boolean = false) {
        clearMapRoute()
        mapRoute = MapRoute.from(route).setType(MapRoute.RouteType.Primary).build().apply {
            addMapObject(this)
        }
        if (createStartMarker) {
            startMarker = MapMarker.at(route.start.navigablePosition).build().apply {
                addMapObject(this)
            }
        }
        destinationMarker = MapMarker.at(route.destination.navigablePosition).build().apply {
            addMapObject(this)
        }
    }

    fun clearMapRoute() {
        mapRoute?.let {
            removeMapObject(it)
        }
        mapRoute = null
        destinationMarker?.let {
            removeMapObject(it)
        }
        destinationMarker = null
        startMarker?.let {
            removeMapObject(it)
        }
        startMarker = null
    }
}
