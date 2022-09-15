package com.sygic.sdk.example.fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sygic.sdk.example.common.data.MapFragmentDataModel
import com.sygic.sdk.example.common.ktx.SdkRouter
import com.sygic.sdk.map.Camera
import com.sygic.sdk.map.MapAnimation
import com.sygic.sdk.map.data.SimpleCameraDataModel
import com.sygic.sdk.position.GeoCoordinates
import com.sygic.sdk.route.RouteRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SdkRoutePreviewMapFragmentViewModel @Inject constructor(
    val cameraDataModel: SimpleCameraDataModel,
    val mapDataModel: MapFragmentDataModel,
    private val sdkRouter: SdkRouter
) : ViewModel() {
    init {
        initCamera()
        calculateRoute()
    }

    private fun initCamera() {
        with(cameraDataModel) {
            movementMode = Camera.MovementMode.Free
            rotationMode = Camera.RotationMode.NorthUp
            tilt = 0F
            zoomLevel = 14F
            position = LondonStart
        }
    }

    override fun onCleared() {
        mapDataModel.clearData()
        super.onCleared()
    }

    private fun calculateRoute() {
        viewModelScope.launch {
            val routeRequest = RouteRequest().apply {
                setStart(LondonStart)
                setDestination(LondonDestination)
            }
            val route = sdkRouter.computeRoute(routeRequest) ?: return@launch

            mapDataModel.setMapRoute(route, true)
            cameraDataModel.setMapRectangle(route.boundingBox, RouteMargin, RouteMargin, RouteMargin, RouteMargin, CameraAnimation)
        }
    }

    companion object {
        private val LondonStart = GeoCoordinates(51.50853,  -0.12574)
        private val LondonDestination = GeoCoordinates(51.504315, -0.100689)
        private const val RouteMargin = 0.1F
        private val CameraAnimation = MapAnimation(200, MapAnimation.InterpolationCurve.Linear)
    }
}