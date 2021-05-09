package com.edwin.weatherapp.util

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide

fun ImageView.loadImage(context: Context, imageId: String?) {
    Glide.with(context)
        .load("https://openweathermap.org/img/wn/$imageId@2x.png")
        .into(this)
}