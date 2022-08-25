package com.sygic.sdk.example.activity

import android.Manifest
import android.content.pm.PackageManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sygic.sdk.example.common.ktx.SdkGpsManager
import com.sygic.sdk.example.common.ktx.SdkInitManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SdkActivityViewModel @Inject constructor(
    private val sdkInitManager: SdkInitManager,
    private val sdkGpsManager: SdkGpsManager
) : ViewModel() {

    private val requestPermissionMutable = MutableLiveData<String>()
    val requestPermission: LiveData<String> = requestPermissionMutable

    fun init() {
        viewModelScope.launch {
            sdkInitManager.initSdk()
            requestPermissionMutable.postValue(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    fun onPermissionRequestResult(permission: String, result: Int) {
        if (permission == Manifest.permission.ACCESS_FINE_LOCATION && result == PackageManager.PERMISSION_GRANTED) {
            // start listening for GPS inside Sygic SDK
            sdkGpsManager.openGpsConnection()
        }
    }
}
