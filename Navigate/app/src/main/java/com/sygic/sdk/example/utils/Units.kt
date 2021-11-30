package com.sygic.sdk.example.utils

import okhttp3.internal.format
import java.util.concurrent.TimeUnit

private const val HairSpace = " "

object Units {
    fun formatSecondsToClock(seconds: Int): String {
        val hours = TimeUnit.SECONDS.toHours(seconds.toLong())
        val minutes = TimeUnit.SECONDS.toMinutes(seconds.toLong()) - TimeUnit.HOURS.toMinutes(hours)
        return format("%02d:%02d", hours, minutes)
    }

    fun formatMeters(meters: Int): String {
        return when {
            meters <= 1200 -> "${meters}${HairSpace}m"
            else -> "${meters / 1000}${HairSpace}km"
        }
    }
}