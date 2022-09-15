package com.sygic.sdk.example.fragment

import androidx.lifecycle.ViewModel
import com.sygic.sdk.map.Camera
import com.sygic.sdk.map.data.SimpleCameraDataModel
import com.sygic.sdk.position.GeoCoordinates
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SdkBrowseMapFragmentViewModel @Inject constructor(
    val cameraDataModel: SimpleCameraDataModel
) : ViewModel() {
    init {
        initCamera()
    }

    private fun initCamera() {
        with(cameraDataModel) {
            movementMode = Camera.MovementMode.Free
            rotationMode = Camera.RotationMode.NorthUp
            tilt = 0F
            zoomLevel = 6F
            position = London
        }
    }

    companion object {
        private val London = GeoCoordinates(51.50853,  -0.12574)
    }
}