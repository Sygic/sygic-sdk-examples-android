package com.sygic.sdk.example.utils

import com.sygic.sdk.example.R
import com.sygic.sdk.example.fragment.data.TextHolder
import com.sygic.sdk.navigation.routeeventnotifications.DirectionInfo

fun DirectionInfo.createInstructionText(): TextHolder {
    if (distance > 2000) {
        return TextHolder.from(R.string.follow_the_route)
    }

    return primary.createInstructionText()
}