package com.sygic.sdk.example.fragment.data

import android.content.Context
import androidx.annotation.StringRes

private const val NO_ID = -1
private const val EMPTY_STRING = ""

class TextHolder private constructor(
    @StringRes private var textResource: Int = NO_ID,
    private var textString: String = EMPTY_STRING
) {

    companion object {
        val empty = TextHolder()

        fun from(@StringRes resId: Int): TextHolder {
            return TextHolder(textResource = resId)
        }

        fun from(text: String): TextHolder {
            return TextHolder(textString = text)
        }

        fun from(@StringRes resId: Int, text: String): TextHolder {
            return TextHolder(textResource = resId, textString = text)
        }

        fun from(@StringRes resId: Int, number: Int): TextHolder {
            return TextHolder(textResource = resId, textString = number.toString())
        }
    }

    fun getText(context: Context): String {
        if (textResource != NO_ID && textString.isNotEmpty()) {
            return String.format(context.getString(textResource), textString)
        }

        if (textResource != NO_ID) {
            return context.getString(textResource)
        }

        return textString
    }
}