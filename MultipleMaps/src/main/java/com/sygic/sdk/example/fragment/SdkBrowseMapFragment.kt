package com.sygic.sdk.example.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.sygic.sdk.example.databinding.FragmentSdkBrowseMapBinding
import com.sygic.sdk.map.Camera
import com.sygic.sdk.map.MapFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SdkBrowseMapFragment : MapFragment() {
    private lateinit var binding: FragmentSdkBrowseMapBinding
    private val viewModel: SdkBrowseMapFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSdkBrowseMapBinding.inflate(inflater, container, false)

        // We want to add map View as a child of id/map frame layout
        val mapView = super.onCreateView(inflater, binding.map, savedInstanceState)
        binding.map.addView(mapView, 0)

        return binding.root
    }

    override fun getCameraDataModel(): Camera.CameraModel {
        return viewModel.cameraDataModel
    }
}
