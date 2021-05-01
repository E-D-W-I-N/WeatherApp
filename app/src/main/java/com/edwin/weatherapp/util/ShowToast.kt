package com.edwin.weatherapp.util

import android.widget.Toast
import androidx.fragment.app.Fragment

fun Fragment.showToast(message: String, length: Int) {
    Toast.makeText(
        requireContext(),
        message,
        length
    ).show()
}