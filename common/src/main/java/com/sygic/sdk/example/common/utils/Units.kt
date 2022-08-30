package com.sygic.sdk.example.common.utils

import java.text.DateFormat
import java.util.*
import java.util.concurrent.TimeUnit

private const val HairSpace = " "

object Units {
    fun formatDuration(seconds: Int): String {
        val hours = TimeUnit.SECONDS.toHours(seconds.toLong())
        val minutes = TimeUnit.SECONDS.toMinutes(seconds.toLong()) - TimeUnit.HOURS.toMinutes(hours)
        return if (hours > 0) "$hours${HairSpace}h $minutes${HairSpace}min" else "$minutes${HairSpace}min"
    }

    fun formatEstimatedTime(seconds: Int): String {
        val date = Date(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(seconds.toLong()))
        return DateFormat.getTimeInstance(DateFormat.SHORT).format(date)
    }

    fun formatMeters(meters: Int): String {
        return when {
            meters <= 1200 -> "${meters}${HairSpace}m"
            else -> "${meters / 1000}${HairSpace}km"
        }
    }
}
