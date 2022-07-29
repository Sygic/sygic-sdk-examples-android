package com.sygic.sdk.example.fragment

import android.view.MotionEvent
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sygic.sdk.example.R
import com.sygic.sdk.example.common.data.MapClickResult
import com.sygic.sdk.example.common.ktx.*
import com.sygic.sdk.example.common.data.MapFragmentDataModel
import com.sygic.sdk.example.common.extensions.subtitle
import com.sygic.sdk.example.common.extensions.title
import com.sygic.sdk.example.fragment.data.NavigationInfo
import com.sygic.sdk.map.*
import com.sygic.sdk.map.data.SimpleCameraDataModel
import com.sygic.sdk.route.RouteRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SdkMapFragmentViewModel @Inject constructor(
    private val navigationManager: SdkNavigationManager,
    private val reverseGeocoder: SdkReverseGeocoder,
    private val positionManager: SdkPositionManager,
    private val voiceManager: SdkVoiceManager,
    private val sdkRouter: SdkRouter,
    val cameraDataModel: SimpleCameraDataModel,
    val mapDataModel: MapFragmentDataModel
) : ViewModel() {

    private val mapAnimation = MapAnimation(200L, MapAnimation.InterpolationCurve.Decelerate)
    private val mapCenterMiddle = MapCenter(0.5F, 0.5F)
    private val mapCenterNavigation = MapCenter(0.5F, 0.25F)
    private val mapCenterSettingsNavigation =
        MapCenterSettings(mapCenterNavigation, mapCenterNavigation, mapAnimation, mapAnimation)
    private val mapCenterSettingsBrowseMap =
        MapCenterSettings(mapCenterMiddle, mapCenterMiddle, mapAnimation, mapAnimation)
    private var mapMode: MapMode = MapMode.BROWSE_MAP

    private val gpsStateDrawableMutable = MutableLiveData<Int>()
    val gpsStateDrawable: LiveData<Int> = gpsStateDrawableMutable

    private val mapClickResultMutable = MutableLiveData<MapClickResult?>()
    val mapClickResult: LiveData<MapClickResult?> = mapClickResultMutable

    private val navigationInfoMutable = MutableLiveData<NavigationInfo?>()
    val navigationInfo: LiveData<NavigationInfo?> = navigationInfoMutable

    private var voiceGuidanceEnabled = true
    private val voiceGuidanceIconMutable = MutableLiveData(R.drawable.ic_volume_on)
    val voiceGuidanceIcon: LiveData<Int> = voiceGuidanceIconMutable

    private enum class MapMode {
        BROWSE_MAP,
        NAVIGATION
    }

    init {
        initCameraModeListener()
        viewModelScope.launch {
            navigationManager.currentRoute()?.let {
                setMode(MapMode.NAVIGATION)
            } ?: setMode(MapMode.BROWSE_MAP)
        }
        viewModelScope.launch {
            initVoice()
        }
    }

    override fun onCleared() {
        mapDataModel.clearData()
        super.onCleared()
    }

    private suspend fun setMode(mapMode: MapMode) {
        this.mapMode = mapMode
        if (mapMode == MapMode.NAVIGATION) {
            navigationManager.setAudioInstructions(voiceGuidanceEnabled)
            navigationManager.currentRoute()?.let {
                mapDataModel.setMapRoute(it)
                navigationInfoMutable.postValue(NavigationInfo.fromRouteInfo(it.routeInfo))
            }
            lockCamera()
            cameraDataModel.tilt = 60F
            cameraDataModel.mapCenterSettings = mapCenterSettingsNavigation
            navigationManager.routeChanged().collect {
                it?.let {
                    mapDataModel.setMapRoute(it)
                    navigationInfoMutable.postValue(NavigationInfo.fromRouteInfo(it.routeInfo))
                }
            }
        } else {
            navigationManager.stopNavigation()
            mapDataModel.clearMapRoute()
            navigationInfoMutable.postValue(null)

            with(cameraDataModel) {
                movementMode = Camera.MovementMode.Free
                rotationMode = Camera.RotationMode.Free
                mapCenterSettings = mapCenterSettingsBrowseMap
                this.tilt = 0F
                zoomLevel = 14F
                position = positionManager.lastKnownPosition().takeIf { it.isValid() }?.coordinates
                    ?: positionManager.position().coordinates
            }
        }
    }

    fun dayNightToggle(isNightMode: Boolean) {
        if (isNightMode) {
            mapDataModel.skin = listOf("day")
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            mapDataModel.skin = listOf("night")
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    fun toggleVoiceGuidance() {
        viewModelScope.launch {
            voiceGuidanceEnabled = voiceGuidanceEnabled.not()
            navigationManager.setAudioInstructions(voiceGuidanceEnabled)
            voiceGuidanceIconMutable.value =
                if (voiceGuidanceEnabled) R.drawable.ic_volume_on else R.drawable.ic_volume_off
        }
    }

    fun lockCamera() {
        with(cameraDataModel) {
            movementMode = Camera.MovementMode.FollowGpsPositionWithAutozoom
            rotationMode = Camera.RotationMode.Vehicle
        }
    }

    private fun initCameraModeListener() {
        cameraDataModel.addModeChangedListener(object : Camera.ModeChangedListener {
            override fun onMovementModeChanged(@Camera.MovementMode movementMode: Int) {
                emitGpsStateDrawable(movementMode)
            }

            override fun onRotationModeChanged(@Camera.RotationMode rotationMode: Int) {}
        })
        emitGpsStateDrawable(cameraDataModel.movementMode)
    }

    fun emitGpsStateDrawable(@Camera.MovementMode movementMode: Int) {
        gpsStateDrawableMutable.postValue(
            when (movementMode) {
                Camera.MovementMode.FollowGpsPosition,
                Camera.MovementMode.FollowGpsPositionWithAutozoom -> R.drawable.ic_gps_locked
                else -> R.drawable.ic_gps_unlocked
            }
        )
    }

    fun onMapClicked(mapView: MapView, event: MotionEvent): Boolean {
        if (mapMode == MapMode.NAVIGATION) {
            return false
        }
        mapClickResultMutable.postValue(null)
        mapView.requestObjectsAtPoint(event.x, event.y) { viewObjects, _, _, _ ->
            viewObjects.firstOrNull()?.let { viewObject ->
                viewModelScope.launch {
                    reverseGeocoder.reverseGeocode(viewObject.position).firstOrNull()
                        ?.let { geocodingResult ->
                            mapDataModel.setMapResultMarker(viewObject.position)

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

    fun onResultHidden() {
        mapDataModel.clearMapResultMarker()
    }

    fun navigate() {
        viewModelScope.launch {
            val fromPosition = positionManager.position().coordinates
            val toPosition = mapClickResult.value?.position ?: return@launch

            val routeRequest = RouteRequest().apply {
                setStart(fromPosition)
                setDestination(toPosition)
            }
            val route = sdkRouter.computeRoute(routeRequest) ?: return@launch

            navigationManager.stopNavigation()
            mapClickResultMutable.postValue(null)

            navigationManager.setRouteForNavigation(route)
            mapDataModel.setMapRoute(route)

            setMode(MapMode.NAVIGATION)
        }
    }

    fun stopNavigation() {
        viewModelScope.launch {
            setMode(MapMode.BROWSE_MAP)
        }
    }

    private suspend fun initVoice() = withContext(Dispatchers.Default) {
        val installedVoices = voiceManager.getInstalledVoices()
        val defaultVoice = voiceManager.getDefaultTts()
        if (installedVoices.isNotEmpty()) {
            voiceManager.setVoice(installedVoices.firstOrNull {
                it.language.compareTo(defaultVoice, true) == 0
            } ?: installedVoices.firstOrNull {
                it.language.compareTo("en-US", true) == 0
            } ?: installedVoices.first())
        }
    }
}