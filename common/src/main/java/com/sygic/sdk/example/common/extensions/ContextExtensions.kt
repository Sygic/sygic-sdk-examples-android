package com.sygic.sdk.example.common.extensions

import android.content.Context
import android.content.res.Configuration
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt

@ColorInt
fun Context.getColorFromAttr(
    @AttrRes resId: Int, typedValue: TypedValue = TypedValue(),
    resolveRefs: Boolean = true
): Int {
    typedValue.let {
        theme.resolveAttribute(resId, it, resolveRefs)
        return it.data
    }
}

fun Context.isNightMode(): Boolean {
    return (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
}