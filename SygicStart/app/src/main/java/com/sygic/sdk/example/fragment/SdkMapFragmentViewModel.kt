package com.sygic.sdk.example.fragment

import androidx.lifecycle.ViewModel
import com.sygic.sdk.map.Camera
import com.sygic.sdk.map.data.SimpleCameraDataModel
import com.sygic.sdk.position.GeoCoordinates

class SdkMapFragmentViewModel : ViewModel() {

    val cameraDataModel = SimpleCameraDataModel()

    companion object {
        private val London = GeoCoordinates(51.50853,  -0.12574)
    }

    init {
        initCamera()
    }

    private fun initCamera() {
        with(cameraDataModel) {
            movementMode = Camera.MovementMode.Free
            rotationMode = Camera.RotationMode.Free
            tilt = 0F
            zoomLevel = 14F
            position = London
        }
    }
}