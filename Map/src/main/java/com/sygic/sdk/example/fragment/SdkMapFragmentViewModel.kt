package com.sygic.sdk.example.fragment

import android.view.MotionEvent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sygic.sdk.example.R
import com.sygic.sdk.example.common.data.MapFragmentDataModel
import com.sygic.sdk.example.common.extensions.subtitle
import com.sygic.sdk.example.common.extensions.title
import com.sygic.sdk.example.common.ktx.SdkPositionManager
import com.sygic.sdk.example.common.ktx.SdkReverseGeocoder
import com.sygic.sdk.example.common.data.MapClickResult
import com.sygic.sdk.map.Camera
import com.sygic.sdk.map.MapView
import com.sygic.sdk.map.`object`.MapMarker
import com.sygic.sdk.map.data.SimpleCameraDataModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SdkMapFragmentViewModel @Inject constructor(
    private val positionManager: SdkPositionManager,
    private val reverseGeocoder: SdkReverseGeocoder,
    val cameraDataModel: SimpleCameraDataModel,
    val mapDataModel: MapFragmentDataModel
) : ViewModel() {

    private val gpsStateDrawableMutable = MutableLiveData<Int>()
    val gpsStateDrawable: LiveData<Int> = gpsStateDrawableMutable

    private val mapClickResultMutable = MutableLiveData<MapClickResult?>()
    val mapClickResult: LiveData<MapClickResult?> = mapClickResultMutable

    private var mapMarker: MapMarker? = null

    init {
        initCameraModeListener()
        followGps()
    }

    override fun onCleared() {
        clearMapResultMarker()
        super.onCleared()
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
                tilt = 0F
                zoomLevel = 14F
                position = positionManager.lastKnownPosition().takeIf { it.isValid() }?.coordinates
                    ?: positionManager.position().coordinates
            }
        }
    }

    fun onResultHidden() {
        clearMapResultMarker()
    }

    private fun clearMapResultMarker() {
        mapMarker?.let {
            mapDataModel.removeMapObject(it)
        }
        mapMarker = null
        mapClickResultMutable.postValue(null)
    }

    fun onMapClicked(mapView: MapView, event: MotionEvent): Boolean {
        mapClickResultMutable.postValue(null)
        mapView.requestObjectsAtPoint(event.x, event.y) { viewObjects, _, _, _ ->
            viewObjects.firstOrNull()?.let { viewObject ->
                viewModelScope.launch {
                    reverseGeocoder.reverseGeocode(viewObject.position).firstOrNull()
                        ?.let { geocodingResult ->
                            clearMapResultMarker()
                            mapMarker = MapMarker.at(viewObject.position).build().apply {
                                mapDataModel.addMapObject(this)
                            }

                            mapClickResultMutable.postValue(
                                MapClickResult(
                                    geocodingResult.title(),
                                    geocodingResult.subtitle(),
                                    viewObject.position
                                )
                            )
                        }
                }
            }
        }
        return true
    }
}
