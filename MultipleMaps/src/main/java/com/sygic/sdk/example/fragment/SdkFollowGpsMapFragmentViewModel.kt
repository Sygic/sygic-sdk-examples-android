package com.sygic.sdk.example.fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sygic.sdk.example.R
import com.sygic.sdk.example.common.ktx.SdkPositionManager
import com.sygic.sdk.map.Camera
import com.sygic.sdk.map.data.SimpleCameraDataModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SdkFollowGpsMapFragmentViewModel @Inject constructor(
    private val positionManager: SdkPositionManager,
    val cameraDataModel: SimpleCameraDataModel
) : ViewModel() {

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
                movementMode = Camera.MovementMode.FollowGpsPosition
                rotationMode = Camera.RotationMode.Vehicle
                tilt = 60F
                zoomLevel = 14F
                position = positionManager.lastKnownPosition().takeIf { it.isValid() }?.coordinates
                    ?: positionManager.position().coordinates
            }
        }
    }
}
