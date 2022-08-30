package com.sygic.sdk.example.common.extensions

import com.sygic.sdk.search.ReverseGeocodingResult

fun ReverseGeocodingResult.title(): String {
    return if (names.street.isNotEmpty()) {
        if (names.houseNumber.isNotEmpty()) {
            names.street + " " + names.houseNumber
        } else {
            names.street
        }
    } else {
        if (names.city.isNotEmpty()) {
            names.city + " " + names.roadNumbers
        } else {
            names.countryIso
        }
    }
}

fun ReverseGeocodingResult.subtitle(): String {
    return if (names.street.isNotEmpty()) {
        if (names.city.isNotEmpty()) {
            names.city + ", " + names.countryIso
        } else {
            names.countryIso
        }
    } else {
        names.countryIso
    }
}
