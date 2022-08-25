package com.sygic.sdk.example.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sygic.sdk.example.databinding.FragmentSdkMapBinding
import com.sygic.sdk.example.directions.DirectionsViewModel
import com.sygic.sdk.example.common.extensions.isNightMode
import com.sygic.sdk.example.laneguidance.LaneGuidanceViewModel
import com.sygic.sdk.map.Camera
import com.sygic.sdk.map.MapFragment
import com.sygic.sdk.map.MapView
import com.sygic.sdk.map.listeners.OnMapInitListener
import com.sygic.sdk.map.mapgesturesdetector.listener.MapGestureAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@AndroidEntryPoint
class SdkMapFragment : MapFragment() {
    private lateinit var binding: FragmentSdkMapBinding
    private lateinit var bottomSheetResult: BottomSheetBehavior<View>
    private lateinit var bottomSheetNavigation: BottomSheetBehavior<View>

    private val viewModel: SdkMapFragmentViewModel by viewModels()
    private val directionsViewModel: DirectionsViewModel by viewModels()
    private val laneGuidanceViewModel: LaneGuidanceViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSdkMapBinding.inflate(inflater, container, false)
        bottomSheetResult =
            BottomSheetBehavior.from(binding.resultBottomSheet.resultBottomSheetLayout)
        bottomSheetResult.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetNavigation =
            BottomSheetBehavior.from(binding.navigationBottomSheet.navigationBottomSheetLayout)
        bottomSheetNavigation.state = BottomSheetBehavior.STATE_HIDDEN

        // We want to add map View as a child of id/map frame layout
        val mapView = super.onCreateView(inflater, binding.map, savedInstanceState)
        binding.map.addView(mapView, 0)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDirections()
        initLaneGuidance()

        viewModel.gpsStateDrawable.observe(viewLifecycleOwner) {
            binding.fabFollowGps.setIconResource(it)
        }
        viewModel.voiceGuidanceIcon.observe(viewLifecycleOwner) {
            binding.navigationBottomSheet.btnVoiceGuidance.setIconResource(it)
        }
        viewModel.mapClickResult.observe(viewLifecycleOwner) { mapClickResult ->
            bottomSheetResult.state = mapClickResult?.let {
                binding.resultBottomSheet.title.text = mapClickResult.title
                binding.resultBottomSheet.subtitle.text = mapClickResult.subtitle
                binding.resultBottomSheet.calculateRouteProgress.visibility = View.INVISIBLE
                binding.resultBottomSheet.fabNavigation.visibility = View.VISIBLE
                BottomSheetBehavior.STATE_COLLAPSED
            } ?: BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetResult.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, state: Int) {
                if (state == BottomSheetBehavior.STATE_HIDDEN) {
                    viewModel.onResultHidden()
                }
            }

            override fun onSlide(p0: View, p1: Float) {
            }
        })

        viewModel.navigationInfo.observe(viewLifecycleOwner) { navigationInfo ->
            if (navigationInfo != null) {
                bottomSheetNavigation.state = BottomSheetBehavior.STATE_COLLAPSED
                binding.navigationBottomSheet.firstLine.text = navigationInfo.firstLine
                binding.navigationBottomSheet.secondLine.text = navigationInfo.secondLine
                binding.navigationDirectionsLayout.signpostContainer.visibility = View.VISIBLE
            } else {
                binding.navigationDirectionsLayout.signpostContainer.visibility = View.GONE
                bottomSheetNavigation.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }

        bottomSheetNavigation.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, state: Int) {
                if (state == BottomSheetBehavior.STATE_HIDDEN) {
                    viewModel.stopNavigation()
                }
            }

            override fun onSlide(p0: View, p1: Float) {
            }
        })

        binding.navigationBottomSheet.btnVoiceGuidance.setOnClickListener { viewModel.toggleVoiceGuidance() }
        binding.navigationBottomSheet.btnCancelRoute.setOnClickListener { viewModel.stopNavigation() }

        binding.fabFollowGps.setOnClickListener { viewModel.lockCamera() }
        binding.fabDayNightMode.setOnClickListener { viewModel.dayNightToggle(requireContext().isNightMode()) }
        binding.resultBottomSheet.fabNavigation.setOnClickListener {
            binding.resultBottomSheet.calculateRouteProgress.visibility = View.VISIBLE
            it.visibility = View.INVISIBLE
            viewModel.navigate()
        }

        lifecycleScope.launch {
            getMapViewAsync().let { mapView ->
                mapView.addMapGestureListener(object : MapGestureAdapter() {
                    override fun onMapClicked(event: MotionEvent?, isTwoFingers: Boolean): Boolean {
                        event?.let {
                            return viewModel.onMapClicked(mapView, it)
                        }
                        return super.onMapClicked(event, isTwoFingers)
                    }
                })
            }
        }
    }

    override fun getCameraDataModel(): Camera.CameraModel {
        return viewModel.cameraDataModel
    }

    override fun getMapDataModel(): MapView.MapDataModel {
        return viewModel.mapDataModel
    }

    private suspend fun getMapViewAsync(): MapView {
        return suspendCoroutine {
            getMapAsync(object : OnMapInitListener {
                override fun onMapInitializationInterrupted() {
                    it.resumeWithException(Throwable("Unable to get map view"))
                }

                override fun onMapReady(mapView: MapView) {
                    it.resume(mapView)
                }
            })
        }
    }

    private fun initDirections() {
        directionsViewModel.distance.observe(viewLifecycleOwner) {
            binding.navigationDirectionsLayout.distanceTextView.text = it
        }
        directionsViewModel.primaryDirection.observe(viewLifecycleOwner) {
            binding.navigationDirectionsLayout.primaryDirectionImageView.setImageResource(it)
        }
        directionsViewModel.instructionText.observe(viewLifecycleOwner) {
            binding.navigationDirectionsLayout.instructionTextView.text =
                it.getText(requireContext())
        }
        directionsViewModel.secondaryDirection.observe(viewLifecycleOwner) {
            binding.navigationDirectionsLayout.secondaryDirectionImageView.setImageResource(it)
        }
        directionsViewModel.secondaryDirectionText.observe(viewLifecycleOwner) {
            binding.navigationDirectionsLayout.secondaryDirectionTextView.text =
                requireContext().getString(it)
        }
        directionsViewModel.secondaryDirectionContainerVisible.observe(viewLifecycleOwner) {
            binding.navigationDirectionsLayout.secondaryDirectionContainer.visibility =
                if (it == true) View.VISIBLE else View.GONE
        }
    }

    private fun initLaneGuidance() {
        laneGuidanceViewModel.lanesData.observe(viewLifecycleOwner) {
            binding.simpleLanesView.lanesData = it
        }
        laneGuidanceViewModel.isActive.observe(viewLifecycleOwner) {
            binding.simpleLanesView.visibility = if (it) View.VISIBLE else View.INVISIBLE
        }
    }
}
