package com.sygic.sdk.example.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sygic.sdk.example.R
import com.sygic.sdk.example.databinding.FragmentSdkMapBinding
import com.sygic.sdk.example.common.extensions.hideKb
import com.sygic.sdk.map.Camera
import com.sygic.sdk.map.MapFragment
import com.sygic.sdk.map.MapView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SdkMapFragment : MapFragment() {
    private lateinit var binding: FragmentSdkMapBinding
    private lateinit var bottomSheet: BottomSheetBehavior<View>
    private val viewModel: SdkMapFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSdkMapBinding.inflate(inflater, container, false)
        bottomSheet = BottomSheetBehavior.from(binding.bottomSheet.bottomSheetLayout)
        bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN

        // We want to add map View as a child of id/map frame layout
        val mapView = super.onCreateView(inflater, binding.map, savedInstanceState)
        binding.map.addView(mapView, 0)

        val divider = DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL).apply {
            ContextCompat.getDrawable(requireContext(), R.drawable.recycler_view_divider)?.let {
                setDrawable(it)
            }
        }
        val adapter = SearchResultsRecyclerAdapter { clickedResult ->
            hideKb()
            viewModel.onSearchItemClick(clickedResult)
        }
        binding.searchResults.layoutManager = LinearLayoutManager(context)
        binding.searchResults.addItemDecoration(divider)
        binding.searchResults.adapter = adapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchInput.addTextChangedListener(afterTextChanged = viewModel::onSearchTextChanged)

        viewModel.searchResults.observe(viewLifecycleOwner) {
            (binding.searchResults.adapter as SearchResultsRecyclerAdapter).setData(it)
        }

        viewModel.mapResult.observe(viewLifecycleOwner) { mapResult ->
            bottomSheet.state = mapResult?.let {
                binding.bottomSheet.title.text = mapResult.title
                binding.bottomSheet.subtitle.text = mapResult.subtitle
                BottomSheetBehavior.STATE_COLLAPSED
            } ?: BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheet.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, state: Int) {
                if (state == BottomSheetBehavior.STATE_HIDDEN) {
                    viewModel.onResultHidden()
                }
            }

            override fun onSlide(p0: View, p1: Float) {
            }
        })
    }

    override fun getCameraDataModel(): Camera.CameraModel {
        return viewModel.cameraDataModel
    }

    override fun getMapDataModel(): MapView.MapDataModel {
        return viewModel.mapDataModel
    }
}