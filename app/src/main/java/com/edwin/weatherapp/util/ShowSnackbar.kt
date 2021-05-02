package com.edwin.weatherapp.util

import android.view.View
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.edwin.weatherapp.R
import com.google.android.material.snackbar.Snackbar

inline fun Fragment.showSnackbar(
    message: String,
    length: Int = Snackbar.LENGTH_LONG,
    f: Snackbar.() -> Unit
) {
    val snack = Snackbar.make(requireView(), message, length)
        .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.white))
        .setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        .setActionTextColor(ContextCompat.getColor(requireContext(), R.color.black))
    snack.f()
    snack.show()
}

fun Snackbar.action(@StringRes action: Int, color: Int? = null, listener: (View) -> Unit) {
    setAction(action, listener)
    color?.let { setActionTextColor(color) }
}