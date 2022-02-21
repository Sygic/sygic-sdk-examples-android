package com.sygic.sdk.example.laneguidance.view

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.util.Pools
import com.sygic.sdk.example.R
import com.sygic.sdk.example.laneguidance.data.LanesData
import com.sygic.sdk.example.extensions.dpToPixels
import com.sygic.sdk.example.extensions.getColorFromAttr

private const val POOL_SIZE = 10

class SimpleLanesView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.lanesViewStyle,
    defStyleRes: Int = R.style.LanesViewStyle
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    var lanesData: Array<LanesData> = emptyArray()
        set(value) {
            field = value
            setLanesInternal(value)
        }

    private var layoutMargin: Int = 0
    private var layoutMarginTop: Int = 0
    private var layoutMarginBottom: Int = 0
    private var layoutMarginStart: Int = 0
    private var layoutMarginEnd: Int = 0

    @ColorInt
    private val laneHighlightedColor = context.getColorFromAttr(R.attr.directionColor)

    @ColorInt
    private val laneColor = context.getColorFromAttr(R.attr.directionShade)
    private val laneViewSize = resources.getDimensionPixelSize(R.dimen.simpleLaneSize)

    private val viewPool: Pools.SimplePool<ImageView> = Pools.SimplePool(POOL_SIZE)

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_HORIZONTAL
        resources.dpToPixels(8f).toInt().let { setPadding(it, it, it, it) }

        attrs?.let { attributeSet ->
            context.obtainStyledAttributes(
                attributeSet,
                R.styleable.SimpleLanesView,
                defStyleAttr,
                defStyleRes
            ).also {
                layoutMargin = it.getDimensionPixelSize(
                    R.styleable.SimpleLanesView_android_layout_margin,
                    -1
                )
                layoutMarginTop = it.getDimensionPixelSize(
                    R.styleable.SimpleLanesView_android_layout_marginTop,
                    0
                )
                layoutMarginBottom = it.getDimensionPixelSize(
                    R.styleable.SimpleLanesView_android_layout_marginBottom,
                    0
                )
                layoutMarginStart = it.getDimensionPixelSize(
                    R.styleable.SimpleLanesView_android_layout_marginStart,
                    0
                )
                layoutMarginEnd = it.getDimensionPixelSize(
                    R.styleable.SimpleLanesView_android_layout_marginEnd,
                    0
                )
            }.recycle()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        with((layoutParams as MarginLayoutParams)) {
            if (layoutMargin >= 0) {
                setMargins(layoutMargin, layoutMargin, layoutMargin, layoutMargin)
            } else {
                setMargins(layoutMarginStart, layoutMarginTop, layoutMarginEnd, layoutMarginBottom)
            }
        }
    }

    private fun setLanesInternal(lanes: Array<LanesData>) {
        val lanesCount = lanes.size

        if (lanesCount < childCount) {
            for (i in childCount - 1 downTo lanesCount) {
                viewPool.release(getChildAt(i))
                removeViewAt(i)
            }
        } else if (lanesCount > childCount) {
            for (i in childCount until lanesCount) {
                addView(viewPool.acquire() ?: createLaneView())
            }
        }

        lanes.reversedArray().forEachIndexed { i, lane ->
            val drawables = lane.directions.map {
                ContextCompat.getDrawable(context, it)
            }.toTypedArray()

            getChildAt(i).setImageDrawable(LayerDrawable(drawables).apply {
                mutate()
                setTintMode(PorterDuff.Mode.SRC_ATOP)
                if (lane.highlighted) {
                    setTint(laneHighlightedColor)
                } else {
                    setTint(laneColor)
                }
            })
        }
    }

    private fun createLaneView() = ImageView(context).apply {
        setBackgroundColor(Color.TRANSPARENT)
        layoutParams = LayoutParams(laneViewSize, laneViewSize)
        scaleType = ImageView.ScaleType.FIT_CENTER
    }

    override fun getChildAt(index: Int) = super.getChildAt(index) as ImageView

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        if (child is ImageView) {
            super.addView(child, index, params)
        } else {
            throw UnsupportedOperationException("You can't add views to ${this::class.java.name}")
        }
    }
}
