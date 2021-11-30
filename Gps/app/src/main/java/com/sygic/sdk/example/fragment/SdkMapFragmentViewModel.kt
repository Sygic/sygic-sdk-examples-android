package com.sygic.sdk.example.fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sygic.sdk.example.R
import com.sygic.sdk.example.ktx.SdkPositionManager
import com.sygic.sdk.map.Camera
import com.sygic.sdk.map.MapAnimation
import com.sygic.sdk.map.data.SimpleCameraDataModel
import kotlinx.coroutines.launch

private val CameraAnimation = MapAnimation(200, MapAnimation.InterpolationCurve.Linear)

class SdkMapFragmentViewModel : ViewModel() {

    val cameraDataModel = SimpleCameraDataModel()
    private val positionManager = SdkPositionManager()

    private val gpsStateDrawableMutable = MutableLiveData<Int>()
    val gpsStateDrawable: LiveData<Int> = gpsStateDrawableMutable

    init {
        initCameraModeListener()
        followGps()
    }

    private fun initCameraModeListener() {
        cameraDataModel.addModeChangedListener(object : Camera.ModeChangedListener {
            override fun onMovementModeChanged(@Camera.MovementMode movementMode: Int) {
                gpsStateDrawableMutable.postValue(
                    when (movementMode) {
                        Camera.MovementMode.FollowGpsPosition,
                        Camera.MovementMode.FollowGpsPositionWithAutozoom -> R.drawable.ic_gps_locked
                        else -> R.drawable.ic_gps_unlocked
                    }
                )
            }

            override fun onRotationModeChanged(@Camera.RotationMode rotationMode: Int) {}
        })
    }

    fun followGps() {
        viewModelScope.launch {
            with(cameraDataModel) {
                movementMode = Camera.MovementMode.FollowGpsPositionWithAutozoom
                rotationMode = Camera.RotationMode.Vehicle
                setTilt(60F, CameraAnimation)
                setZoomLevel(14F, CameraAnimation)
                position = positionManager.lastKnownPosition().takeIf { it.isValid() }?.coordinates ?: positionManager.position().coordinates
            }
        }
    }

    fun northUp() {
        cameraDataModel.setRotation(0F, CameraAnimation)
    }

    fun zoomIn() {
        val wantedZoomLevel = cameraDataModel.zoomLevel + 1f
        cameraDataModel.setZoomLevel(wantedZoomLevel, CameraAnimation)
    }

    fun zoomOut() {
        val wantedZoomLevel = cameraDataModel.zoomLevel - 1f
        cameraDataModel.setZoomLevel(wantedZoomLevel, CameraAnimation)
    }

    fun toggle2D3D() {
        if (cameraDataModel.tilt == 0F) {
            cameraDataModel.setTilt(60F, CameraAnimation)
        } else {
            cameraDataModel.setTilt(0F, CameraAnimation)
        }
    }
}