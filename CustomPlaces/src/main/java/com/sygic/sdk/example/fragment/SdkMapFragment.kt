package com.sygic.sdk.example.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.viewModels
import com.sygic.sdk.example.R
import com.sygic.sdk.example.databinding.FragmentSdkMapBinding
import com.sygic.sdk.map.Camera
import com.sygic.sdk.map.MapFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.placesAdded.observe(viewLifecycleOwner) {
            val icon = if (it) R.drawable.ic_remove else R.drawable.ic_add
            binding.fabAddRemoveCustomPlaces.icon = AppCompatResources.getDrawable(requireContext(), icon)
        }

        viewModel.toastMessage.observe(viewLifecycleOwner) {
            if (it != null) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.messageShown()
            }
        }

        binding.fabAddRemoveCustomPlaces.setOnClickListener {
            viewModel.addOrRemoveCustomPlaces()
        }
    }

    override fun getCameraDataModel(): Camera.CameraModel {
        return viewModel.cameraDataModel
    }
}
