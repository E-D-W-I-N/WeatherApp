package com.edwin.weatherapp.presentation.weatherDetails

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.edwin.domain.DataResult
import com.edwin.weatherapp.R
import com.edwin.weatherapp.databinding.WeatherDetailsFragmentBinding
import com.edwin.weatherapp.util.action
import com.edwin.weatherapp.util.loadImage
import com.edwin.weatherapp.util.showSnackbar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class WeatherDetailsFragment : Fragment(R.layout.weather_details_fragment) {

    private val viewModel: WeatherDetailsViewModel by viewModel()
    private val args: WeatherDetailsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = WeatherDetailsFragmentBinding.bind(view)
        weatherScreenSetup(binding)
        setHasOptionsMenu(true)
    }

    private fun weatherScreenSetup(binding: WeatherDetailsFragmentBinding) = with(binding) {
        val latitude = args.latitude
        val longitude = args.longitude
        viewModel.getWeatherDetails(latitude, longitude)
        lifecycleScope.launch {
            val flow =
                viewModel.weatherDetails.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            flow.collect { result ->
                when (result) {
                    is DataResult.Success -> {
                        progressBar.visibility = View.INVISIBLE
                        val weatherDetails = result.data
                        temperature.text = weatherDetails?.temperature.toString()
                        weatherIcon.loadImage(requireContext(), weatherDetails?.icon)
                        weatherBrief.text = weatherDetails?.generalInfo
                        humidity.text = getString(R.string.humidity_value, weatherDetails?.humidity)
                        wind.text = getString(R.string.wind_value, weatherDetails?.windSpeed)
                        pressure.text = getString(R.string.pressure_value, weatherDetails?.pressure)
                        setWeatherImage(weatherDetails?.generalInfo, binding)
                        weatherDetailsScreen.visibility = View.VISIBLE
                    }
                    is DataResult.Error -> {
                        progressBar.visibility = View.INVISIBLE
                        textViewError.text = result.errorMessage
                        textViewError.visibility = View.VISIBLE
                    }
                    is DataResult.Loading -> {
                        progressBar.visibility = View.VISIBLE
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun setWeatherImage(generalInfo: String?, binding: WeatherDetailsFragmentBinding) {
        when (generalInfo) {
            "scattered clouds" -> binding.weatherImage.setImageResource(R.drawable.scattered_clouds)
            "clear sky" -> binding.weatherImage.setImageResource(R.drawable.clear_sky)
            "rain" -> binding.weatherImage.setImageResource(R.drawable.rain)
            "thunderstorm" -> binding.weatherImage.setImageResource(R.drawable.thunderstorm)
            "few clouds" -> binding.weatherImage.setImageResource(R.drawable.few_clouds)
            "broken clouds" -> binding.weatherImage.setImageResource(R.drawable.broken_clouds)
            "shower rain" -> binding.weatherImage.setImageResource(R.drawable.shower_rain)
            "snow" -> binding.weatherImage.setImageResource(R.drawable.snow)
            "mist" -> binding.weatherImage.setImageResource(R.drawable.mist)
            else -> binding.weatherImage.setImageResource(R.drawable.few_clouds)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_weather_details, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_share -> {
                showSnackbar(getString(R.string.share_toast_text)) {
                    action(android.R.string.ok) {
                        this.dismiss()
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}