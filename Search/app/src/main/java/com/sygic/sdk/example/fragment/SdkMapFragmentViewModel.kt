package com.sygic.sdk.example.fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sygic.sdk.example.common.data.MapFragmentDataModel
import com.sygic.sdk.example.common.ktx.SdkPositionManager
import com.sygic.sdk.example.common.ktx.SdkSearchManager
import com.sygic.sdk.example.common.ktx.SdkSearchSession
import com.sygic.sdk.map.Camera
import com.sygic.sdk.map.`object`.MapMarker
import com.sygic.sdk.map.data.SimpleCameraDataModel
import com.sygic.sdk.position.GeoCoordinates
import com.sygic.sdk.search.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SdkMapFragmentViewModel @Inject constructor(
    private val positionManager: SdkPositionManager,
    private val searchManager: SdkSearchManager,
    val cameraDataModel: SimpleCameraDataModel,
    val mapDataModel: MapFragmentDataModel

) : ViewModel() {

    private var mapMarker: MapMarker? = null
    private var search: Search? = null
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
        viewModelScope.launch {
            search = initSearch()
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
            search?.let { search ->
                searchJob?.let { job ->
                    job.cancel()
                    job.join()
                }

                mapResultMutable.postValue(null)

                if (searchSession == null) {
                    searchSession = SdkSearchSession(search.createSession())
                }

                searchJob = launch {
                    val searchCoordinates = positionManager.lastKnownPosition().coordinates
                    searchSession?.search(s.toString(), searchCoordinates).let { results ->
                        searchResultsMutable.postValue(results)
                    }
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

    private suspend fun initSearch(): Search? {
        with(searchManager) {
            val parallelSearches = mutableListOf<Search>().apply {
                createOfflineMapSearch()?.let { add(it) }
                createOnlineMapSearch()?.let { add(it) }
                createCustomPlacesSearch()?.let { add(it) }
            }
            val parallelCompositeSearch =
                createCompositeSearch(SearchManager.CompositeSearchType.Parallel, parallelSearches)

            val sequentialSearches = mutableListOf<Search>().apply {
                createCoordinateSearch()?.let { add(it) }
                parallelCompositeSearch?.let { add(it) }
            }

            return createCompositeSearch(
                SearchManager.CompositeSearchType.Sequential,
                sequentialSearches
            )
        }
    }
}