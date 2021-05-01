package com.edwin.weatherapp.util

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.ImageView
import com.bumptech.glide.Glide

fun ImageView.loadImage(context: Context, imageId: String?) {
    Glide.with(context)
        .load("http://openweathermap.org/img/wn/$imageId@2x.png")
        .placeholder(ColorDrawable(Color.WHITE))
        .into(this)
}