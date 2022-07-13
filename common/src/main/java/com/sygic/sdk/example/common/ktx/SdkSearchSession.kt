package com.sygic.sdk.example.common.ktx

import com.sygic.sdk.position.GeoCoordinates
import com.sygic.sdk.search.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SdkSearchSession(private val session: Session) {

    suspend fun search(
        searchInput: String,
        searchCoordinates: GeoCoordinates
    ): List<AutocompleteResult> {
        return suspendCoroutine {
            val searchRequest = SearchRequest(searchInput, searchCoordinates)

            val searchListener = object : AutocompleteResultListener {
                override fun onAutocomplete(autocompleteResult: List<AutocompleteResult>) {
                    it.resume(autocompleteResult)
                }

                override fun onAutocompleteError(status: ResultStatus) {
                    it.resume(emptyList())
                }
            }
            session.autocomplete(searchRequest, searchListener)
        }
    }

    suspend fun geocode(geocodeLocationRequest: GeocodeLocationRequest): GeocodingResult? {
        return suspendCoroutine {
            val geocodeListener = object : GeocodingResultListener {
                override fun onGeocodingResult(geocodingResult: GeocodingResult) {
                    it.resume(geocodingResult)
                }

                override fun onGeocodingResultError(status: ResultStatus) {
                    it.resume(null)
                }
            }
            session.geocode(geocodeLocationRequest, geocodeListener)
        }
    }
}