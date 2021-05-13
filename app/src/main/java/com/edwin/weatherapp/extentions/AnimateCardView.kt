package com.edwin.weatherapp.extentions

import android.view.View
import androidx.cardview.widget.CardView

fun CardView.animateIn() {
    val startPosition = 400f
    val endPosition = 0f
    val duration = 2000L
    translationY = startPosition
    visibility = View.VISIBLE
    animate()
        .translationY(endPosition)
        .setDuration(duration)
        .start()
}

fun CardView.animateOut() {
    val startPosition = 0f
    val endPosition = 400f
    val duration = 2000L
    translationY = startPosition
    animate()
        .translationY(endPosition)
        .setDuration(duration)
        .withEndAction {
            visibility = View.INVISIBLE
        }
        .start()
}