package com.sygic.sdk.example.fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sygic.sdk.example.ktx.SdkPositionManager
import com.sygic.sdk.example.ktx.SdkSearchManager
import com.sygic.sdk.example.ktx.SdkSearchSession
import com.sygic.sdk.map.Camera
import com.sygic.sdk.map.`object`.MapMarker
import com.sygic.sdk.map.data.SimpleCameraDataModel
import com.sygic.sdk.map.data.SimpleMapDataModel
import com.sygic.sdk.position.GeoCoordinates
import com.sygic.sdk.search.AutocompleteResult
import com.sygic.sdk.search.GeocodeLocationRequest
import com.sygic.sdk.search.GeocodingResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SdkMapFragmentViewModel : ViewModel() {

    val cameraDataModel = SimpleCameraDataModel()
    val mapDataModel = SimpleMapDataModel()
    private val positionManager = SdkPositionManager()
    private val searchManager = SdkSearchManager()

    private var mapMarker: MapMarker? = null
    private var searchJob: Job? = null
    private var searchSession: SdkSearchSession? = null

    private val searchResultsMutable = MutableLiveData<List<AutocompleteResult>>()
    val searchResults: LiveData<List<AutocompleteResult>> = searchResultsMutable

    private val mapResultMutable = MutableLiveData<GeocodingResult?>()
    val mapResult: LiveData<GeocodingResult?> = mapResultMutable

    init {
        viewModelScope.launch {
            val initialPosition =
                positionManager.lastKnownPosition().takeIf { it.isValid() }?.coordinates
                    ?: positionManager.position().coordinates
            setCamera(initialPosition)
        }
    }

    override fun onCleared() {
        clearMapResultMarker()
        searchJob?.cancel()
        super.onCleared()
    }

    fun onResultHidden() {
        clearMapResultMarker()
    }

    private fun clearMapResultMarker() {
        mapMarker?.let {
            mapDataModel.removeMapObject(it)
        }
        mapMarker = null
    }

    fun onSearchItemClick(result: AutocompleteResult) {
        viewModelScope.launch {
            searchResultsMutable.postValue(emptyList())
            searchSession?.let {
                searchSession = null
                val geocodeLocationRequest = GeocodeLocationRequest(result.locationId)
                val geocodingResult = it.geocode(geocodeLocationRequest)
                searchManager.closeSession(it)
                geocodingResult?.let {
                    clearMapResultMarker()
                    mapMarker = MapMarker.at(geocodingResult.location).build().apply {
                        mapDataModel.addMapObject(this)
                    }
                    setCamera(geocodingResult.location)
                    mapResultMutable.postValue(geocodingResult)
                }
            }
        }
    }

    fun onSearchTextChanged(s: CharSequence?) {
        viewModelScope.launch {
            searchJob?.let {
                it.cancel()
                it.join()
            }

            mapResultMutable.postValue(null)

            if (searchSession == null) {
                searchSession = searchManager.newOnlineSession()
            }

            searchJob = launch {
                val searchCoordinates = positionManager.lastKnownPosition().coordinates
                searchSession?.search(s.toString(), searchCoordinates).let {
                    searchResultsMutable.postValue(it)
                }
            }
        }
    }

    private fun setCamera(geoCoordinates: GeoCoordinates) {
        viewModelScope.launch {
            with(cameraDataModel) {
                movementMode = Camera.MovementMode.Free
                rotationMode = Camera.RotationMode.NorthUp
                tilt = 0F
                zoomLevel = 14F
                position = geoCoordinates
            }
        }
    }
}