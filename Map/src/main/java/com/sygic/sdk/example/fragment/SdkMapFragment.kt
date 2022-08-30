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
    private val viewModel: SdkMapFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSdkMapBinding.inflate(inflater, container, false)
        bottomSheetResult = BottomSheetBehavior.from(binding.bottomSheet.bottomSheetLayout)
        bottomSheetResult.state = BottomSheetBehavior.STATE_HIDDEN

        // We want to add map View as a child of id/map frame layout
        val mapView = super.onCreateView(inflater, binding.map, savedInstanceState)
        binding.map.addView(mapView, 0)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.gpsStateDrawable.observe(viewLifecycleOwner) {
            binding.fabFollowGps.setImageResource(it)
        }
        viewModel.mapClickResult.observe(viewLifecycleOwner) { mapClickResult ->
            bottomSheetResult.state = mapClickResult?.let {
                binding.bottomSheet.title.text = mapClickResult.title
                binding.bottomSheet.subtitle.text = mapClickResult.subtitle
                BottomSheetBehavior.STATE_COLLAPSED
            } ?: BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetResult.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, state: Int) {
                if (state == BottomSheetBehavior.STATE_HIDDEN) {
                    viewModel.onResultHidden()
                }
            }

            override fun onSlide(p0: View, p1: Float) {
            }
        })

        binding.fabFollowGps.setOnClickListener { viewModel.followGps() }

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
}
