package com.sygic.sdk.example.directions.extensions

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.sygic.sdk.example.R
import com.sygic.sdk.example.fragment.data.TextHolder
import com.sygic.sdk.route.RouteManeuver

@DrawableRes
fun RouteManeuver.getDirectionDrawable(): Int = when (type) {
    RouteManeuver.Type.KeepRight -> R.drawable.ic_direction_right_keep
    RouteManeuver.Type.KeepLeft -> R.drawable.ic_direction_left_keep
    RouteManeuver.Type.EasyRight -> R.drawable.ic_direction_right_45
    RouteManeuver.Type.EasyLeft -> R.drawable.ic_direction_left_45
    RouteManeuver.Type.Right -> R.drawable.ic_direction_right_90
    RouteManeuver.Type.Left -> R.drawable.ic_direction_left_90
    RouteManeuver.Type.SharpRight -> R.drawable.ic_direction_right_135
    RouteManeuver.Type.SharpLeft -> R.drawable.ic_direction_left_135
    RouteManeuver.Type.Via -> R.drawable.ic_direction_finish
    RouteManeuver.Type.Straight -> R.drawable.ic_direction_straight
    RouteManeuver.Type.UTurnRight -> R.drawable.ic_direction_right_180
    RouteManeuver.Type.UTurnLeft -> R.drawable.ic_direction_left_180
    RouteManeuver.Type.End -> R.drawable.ic_direction_finish
    RouteManeuver.Type.Start -> R.drawable.ic_direction_finish
    RouteManeuver.Type.RoundaboutSE -> R.drawable.ic_direction_round_45
    RouteManeuver.Type.RoundaboutE -> R.drawable.ic_direction_round_90
    RouteManeuver.Type.RoundaboutNE -> R.drawable.ic_direction_round_135
    RouteManeuver.Type.RoundaboutN -> R.drawable.ic_direction_round_180
    RouteManeuver.Type.RoundaboutNW -> R.drawable.ic_direction_round_225
    RouteManeuver.Type.RoundaboutW -> R.drawable.ic_direction_round_270
    RouteManeuver.Type.RoundaboutSW -> R.drawable.ic_direction_round_315
    RouteManeuver.Type.RoundaboutS -> R.drawable.ic_direction_round_360
    RouteManeuver.Type.RoundaboutLeftSW -> R.drawable.ic_direction_round_left_45
    RouteManeuver.Type.RoundaboutLeftW -> R.drawable.ic_direction_round_left_90
    RouteManeuver.Type.RoundaboutLeftNW -> R.drawable.ic_direction_round_left_135
    RouteManeuver.Type.RoundaboutLeftN -> R.drawable.ic_direction_round_left_180
    RouteManeuver.Type.RoundaboutLeftNE -> R.drawable.ic_direction_round_left_225
    RouteManeuver.Type.RoundaboutLeftE -> R.drawable.ic_direction_round_left_270
    RouteManeuver.Type.RoundaboutLeftSE -> R.drawable.ic_direction_round_left_315
    RouteManeuver.Type.RoundaboutLeftS -> R.drawable.ic_direction_round_left_360
    RouteManeuver.Type.Ferry -> R.drawable.ic_direction_ferry
    RouteManeuver.Type.StateBoundary -> R.drawable.ic_direction_border_crossing
    RouteManeuver.Type.Follow -> R.drawable.ic_direction_straight
    RouteManeuver.Type.ExitRight -> R.drawable.ic_direction_exit_right
    RouteManeuver.Type.ExitLeft -> R.drawable.ic_direction_exit_left
    RouteManeuver.Type.Motorway -> R.drawable.ic_direction_highway
    RouteManeuver.Type.Tunnel -> R.drawable.ic_direction_tunnel
    else -> 0
}

fun RouteManeuver.isRoundabout(): Boolean = when (type) {
    RouteManeuver.Type.RoundaboutE,
    RouteManeuver.Type.RoundaboutN,
    RouteManeuver.Type.RoundaboutNE,
    RouteManeuver.Type.RoundaboutNW,
    RouteManeuver.Type.RoundaboutS,
    RouteManeuver.Type.RoundaboutSE,
    RouteManeuver.Type.RoundaboutSW,
    RouteManeuver.Type.RoundaboutW,
    RouteManeuver.Type.RoundaboutLeftE,
    RouteManeuver.Type.RoundaboutLeftN,
    RouteManeuver.Type.RoundaboutLeftNE,
    RouteManeuver.Type.RoundaboutLeftNW,
    RouteManeuver.Type.RoundaboutLeftS,
    RouteManeuver.Type.RoundaboutLeftSE,
    RouteManeuver.Type.RoundaboutLeftSW,
    RouteManeuver.Type.RoundaboutLeftW -> true
    else -> false
}

@StringRes
fun RouteManeuver.getDirectionInstruction(): Int = when (type) {
    RouteManeuver.Type.KeepRight -> R.string.keep_right
    RouteManeuver.Type.KeepLeft -> R.string.keep_left
    RouteManeuver.Type.EasyRight -> R.string.turn_slightly_right
    RouteManeuver.Type.EasyLeft -> R.string.turn_slightly_left
    RouteManeuver.Type.Right -> R.string.turn_right
    RouteManeuver.Type.Left -> R.string.turn_left
    RouteManeuver.Type.SharpRight -> R.string.sharp_right
    RouteManeuver.Type.SharpLeft -> R.string.sharp_left
    RouteManeuver.Type.Via -> R.string.via
    RouteManeuver.Type.Straight -> R.string.follow_the_route
    RouteManeuver.Type.UTurnRight -> R.string.make_u_turn
    RouteManeuver.Type.UTurnLeft -> R.string.make_u_turn
    RouteManeuver.Type.End -> R.string.finish
    RouteManeuver.Type.Start -> R.string.start
    RouteManeuver.Type.Ferry -> R.string.take_the_ferry
    RouteManeuver.Type.StateBoundary -> R.string.entry_into
    RouteManeuver.Type.Follow -> R.string.follow_the_route
    RouteManeuver.Type.ExitRight -> R.string.exit_right
    RouteManeuver.Type.ExitLeft -> R.string.exit_left
    RouteManeuver.Type.Motorway -> R.string.take_the_motorway
    RouteManeuver.Type.Tunnel -> R.string.tunnel
    RouteManeuver.Type.RoundaboutE,
    RouteManeuver.Type.RoundaboutN,
    RouteManeuver.Type.RoundaboutNE,
    RouteManeuver.Type.RoundaboutNW,
    RouteManeuver.Type.RoundaboutS,
    RouteManeuver.Type.RoundaboutSE,
    RouteManeuver.Type.RoundaboutSW,
    RouteManeuver.Type.RoundaboutW,
    RouteManeuver.Type.RoundaboutLeftE,
    RouteManeuver.Type.RoundaboutLeftN,
    RouteManeuver.Type.RoundaboutLeftNE,
    RouteManeuver.Type.RoundaboutLeftNW,
    RouteManeuver.Type.RoundaboutLeftS,
    RouteManeuver.Type.RoundaboutLeftSE,
    RouteManeuver.Type.RoundaboutLeftSW,
    RouteManeuver.Type.RoundaboutLeftW -> R.string.exit_number
    else -> 0
}

fun RouteManeuver.nextRoadText(): String {
    return if (nextRoadName.isNotEmpty()) nextRoadName else if (nextRoadNumbers.isNotEmpty()) nextRoadNumbers.first() else ""
}


fun RouteManeuver.createInstructionText(): TextHolder {
    if (!isValid) {
        return TextHolder.empty
    }

    if (isRoundabout()) {
        return getDirectionInstruction().let {
            if (it != 0) TextHolder.from(it, roundaboutExit) else TextHolder.empty
        }
    }

    with(nextRoadText()) {
        if (isNotEmpty()) return TextHolder.from(this)
    }

    getDirectionInstruction().let {
        return if (it != 0) TextHolder.from(it) else TextHolder.empty
    }
}
