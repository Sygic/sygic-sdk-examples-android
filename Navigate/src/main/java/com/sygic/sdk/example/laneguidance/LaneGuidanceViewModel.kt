package com.sygic.sdk.example.laneguidance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sygic.sdk.example.R
import com.sygic.sdk.example.common.ktx.SdkNavigationManager
import com.sygic.sdk.example.laneguidance.data.LanesData
import com.sygic.sdk.navigation.routeeventnotifications.LaneInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LaneGuidanceViewModel @Inject constructor(
    private val navigationManager: SdkNavigationManager
) : ViewModel() {

    private val isActiveMutable = MutableLiveData(false)
    val isActive: LiveData<Boolean> = isActiveMutable

    private val lanesDataMutable = MutableLiveData<Array<LanesData>>()
    val lanesData: LiveData<Array<LanesData>> = lanesDataMutable

    init {
        viewModelScope.launch {
            navigationManager.lanes().collect { onLanesInfo(it) }
        }
    }

    private fun onLanesInfo(laneInfo: LaneInfo) {
        if (!laneInfo.isActive) {
            isActiveMutable.value = false
        } else {
            with(laneInfo.simpleLanesInfo) {
                val lanesArray = this?.lanes?.map { lane ->
                    LanesData(
                        lane.arrows.map { it.direction }.map(lanesTransformations)
                            .ifEmpty { listOf(R.drawable.ic_lanedirection_straight) },
                        lane.arrows.firstOrNull { it.isHighlighted }?.isHighlighted ?: false
                    )
                }?.toTypedArray() ?: emptyArray()

                lanesDataMutable.value = lanesArray
                isActiveMutable.value = lanesArray.isNotEmpty()
            }
        }
    }

    private val lanesTransformations: (Int) -> Int = {
        when (it) {
            LaneInfo.Lane.Direction.Right -> R.drawable.ic_lanedirection_right
            LaneInfo.Lane.Direction.HalfRight -> R.drawable.ic_lanedirection_right_half
            LaneInfo.Lane.Direction.SharpRight -> R.drawable.ic_lanedirection_right_sharp
            LaneInfo.Lane.Direction.UTurnRight -> R.drawable.ic_lanedirection_right_uturn
            LaneInfo.Lane.Direction.Left -> R.drawable.ic_lanedirection_left
            LaneInfo.Lane.Direction.HalfLeft -> R.drawable.ic_lanedirection_left_half
            LaneInfo.Lane.Direction.SharpLeft -> R.drawable.ic_lanedirection_left_sharp
            LaneInfo.Lane.Direction.UTurnLeft -> R.drawable.ic_lanedirection_left_uturn
            else -> R.drawable.ic_lanedirection_straight
        }
    }
}
