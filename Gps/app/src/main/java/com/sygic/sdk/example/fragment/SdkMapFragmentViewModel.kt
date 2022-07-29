package com.sygic.sdk.example.fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sygic.sdk.example.R
import com.sygic.sdk.example.common.ktx.SdkPositionManager
import com.sygic.sdk.map.Camera
import com.sygic.sdk.map.MapAnimation
import com.sygic.sdk.map.data.SimpleCameraDataModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

private val CameraAnimation = MapAnimation(200, MapAnimation.InterpolationCurve.Linear)

@HiltViewModel
class SdkMapFragmentViewModel @Inject constructor(
    private val positionManager: SdkPositionManager,
    val cameraDataModel: SimpleCameraDataModel
) : ViewModel() {

    private val gpsStateDrawableMutable = MutableLiveData<Int>()
    val gpsStateDrawable: LiveData<Int> = gpsStateDrawableMutable

    private val cameraStateDrawableMutable = MutableLiveData<Int>()
    val cameraStateDrawable: LiveData<Int> = cameraStateDrawableMutable

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
                cameraStateDrawableMutable.postValue(R.drawable.ic_2d)
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
            cameraStateDrawableMutable.postValue(R.drawable.ic_2d)
            cameraDataModel.setTilt(60F, CameraAnimation)
        } else {
            cameraStateDrawableMutable.postValue(R.drawable.ic_3d)
            cameraDataModel.setTilt(0F, CameraAnimation)
        }
    }
}