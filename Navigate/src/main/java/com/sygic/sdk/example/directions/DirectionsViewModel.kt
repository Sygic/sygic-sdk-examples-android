package com.sygic.sdk.example.directions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sygic.sdk.example.R
import com.sygic.sdk.example.fragment.data.TextHolder
import com.sygic.sdk.example.common.ktx.SdkNavigationManager
import com.sygic.sdk.example.common.utils.Units
import com.sygic.sdk.example.directions.extensions.createInstructionText
import com.sygic.sdk.example.directions.extensions.getDirectionDrawable
import com.sygic.sdk.navigation.routeeventnotifications.DirectionInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DirectionsViewModel @Inject constructor(
    private val navigationManager: SdkNavigationManager
) : ViewModel() {

    private val distanceMutable = MutableLiveData<String>()
    val distance: LiveData<String> = distanceMutable
    private val primaryDirectionMutable = MutableLiveData<Int>()
    val primaryDirection: LiveData<Int> = primaryDirectionMutable
    private val secondaryDirectionMutable = MutableLiveData<Int>()
    val secondaryDirection: LiveData<Int> = secondaryDirectionMutable
    private val secondaryDirectionTextMutable = MutableLiveData(R.string.then)
    val secondaryDirectionText: LiveData<Int> = secondaryDirectionTextMutable
    private val secondaryDirectionContainerVisibleMutable = MutableLiveData(false)
    val secondaryDirectionContainerVisible: LiveData<Boolean> =
        secondaryDirectionContainerVisibleMutable
    private val instructionTextMutable = MutableLiveData<TextHolder>()
    val instructionText: LiveData<TextHolder> = instructionTextMutable

    init {
        viewModelScope.launch {
            navigationManager.directions().collect { onDirectionInfo(it) }
        }
    }

    private fun onDirectionInfo(directionInfo: DirectionInfo) {
        distanceMutable.postValue(Units.formatMeters(directionInfo.distance))
        primaryDirectionMutable.postValue(directionInfo.primary.getDirectionDrawable())
        secondaryDirectionMutable.postValue(directionInfo.secondary.getDirectionDrawable())
        secondaryDirectionContainerVisibleMutable.postValue(directionInfo.secondary.isValid)
        instructionTextMutable.postValue(directionInfo.createInstructionText())
    }
}
