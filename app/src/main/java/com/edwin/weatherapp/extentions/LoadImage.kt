package com.edwin.weatherapp.extentions

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.edwin.weatherapp.BuildConfig

fun ImageView.loadImage(context: Context, imageId: String?) {
    Glide.with(context)
        .load(
            BuildConfig.ICON_URL_START
                    + imageId
                    + BuildConfig.ICON_URL_END
        )
        .into(this)
}