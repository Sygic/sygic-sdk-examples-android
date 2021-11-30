package com.sygic.sdk.example.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.sygic.sdk.example.databinding.FragmentSdkMapBinding
import com.sygic.sdk.map.Camera
import com.sygic.sdk.map.MapFragment

class SdkMapFragment : MapFragment() {
    private lateinit var binding: FragmentSdkMapBinding
    private val viewModel: SdkMapFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSdkMapBinding.inflate(inflater, container, false)

        // We want to add map View as a child of id/map frame layout
        val mapView = super.onCreateView(inflater, binding.map, savedInstanceState)
        binding.map.addView(mapView, 0)

        return binding.root
    }

    override fun getCameraDataModel(): Camera.CameraModel {
        return viewModel.cameraDataModel
    }
}