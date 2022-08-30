package com.sygic.sdk.example.fragment

import android.content.res.AssetManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sygic.sdk.example.common.ktx.SdkCustomPlacesManager
import com.sygic.sdk.map.Camera
import com.sygic.sdk.map.data.SimpleCameraDataModel
import com.sygic.sdk.position.GeoCoordinates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SdkMapFragmentViewModel @Inject constructor(
    val cameraDataModel: SimpleCameraDataModel,
    private val customPlacesManager: SdkCustomPlacesManager,
    private val assetManager: AssetManager
) : ViewModel() {
    private val placesAddedMutable = MutableLiveData(false)
    val placesAdded: LiveData<Boolean> = placesAddedMutable

    private val toastMessageMutable = MutableLiveData<String?>()
    val toastMessage: LiveData<String?> = toastMessageMutable

    init {
        initCamera()
    }

    private fun initCamera() {
        with(cameraDataModel) {
            movementMode = Camera.MovementMode.Free
            rotationMode = Camera.RotationMode.Free
            tilt = 0F
            zoomLevel = 20F
            position = HydeParkLondon
        }
    }

    fun addOrRemoveCustomPlaces() {
        if (placesAdded.value!!) {
            installCustomPlaces("custom_places_remove.json", "Custom places removed")
        } else {
            installCustomPlaces("custom_places_add.json", "Custom places added")
        }
        placesAddedMutable.value = !placesAdded.value!!
    }

    private fun installCustomPlaces(fileName: String, message: String) {
        val customPlacesJson = assetManager.open(fileName).use {
            it.bufferedReader().readText()
        }

        viewModelScope.launch {
            customPlacesManager.installOfflinePlacesFromJson(customPlacesJson)
            toastMessageMutable.value = message
        }
    }

    fun messageShown() {
        toastMessageMutable.value = null
    }

    companion object {
        private val HydeParkLondon = GeoCoordinates(51.50913, -0.18365)
    }
}
