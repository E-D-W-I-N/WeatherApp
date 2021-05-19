package com.edwin.weatherapp.extensions

import android.view.View
import androidx.cardview.widget.CardView

const val positionHidden = 400f
const val positionShown = 0f
const val defaultDuration = 500L

fun CardView.animateIn(
    startPosition: Float = positionHidden,
    endPosition: Float = positionShown,
    duration: Long = defaultDuration
) {
    translationY = startPosition
    visibility = View.VISIBLE
    animate()
        .translationY(endPosition)
        .setDuration(duration)
        .start()
}

fun CardView.animateOut(
    endPosition: Float = positionHidden,
    duration: Long = defaultDuration
) {
    animate()
        .translationY(endPosition)
        .setDuration(duration)
        .withEndAction {
            visibility = View.GONE
        }
        .start()
}