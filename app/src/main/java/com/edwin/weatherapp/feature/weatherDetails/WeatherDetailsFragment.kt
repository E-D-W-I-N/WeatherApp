package com.edwin.weatherapp.feature.weatherDetails

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.edwin.weatherapp.R
import com.edwin.weatherapp.databinding.WeatherDetailsFragmentBinding
import com.edwin.weatherapp.util.loadImage
import org.koin.androidx.viewmodel.ext.android.viewModel

class WeatherDetailsFragment : Fragment(R.layout.weather_details_fragment) {

    private val viewModel: WeatherDetailsViewModel by viewModel()
    private val args: WeatherDetailsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = WeatherDetailsFragmentBinding.bind(view)
        weatherScreenSetup(binding)
    }

    private fun weatherScreenSetup(binding: WeatherDetailsFragmentBinding) = with(binding) {
        val cityName = args.cityName
        viewModel.getWeatherDetails(cityName).observe(viewLifecycleOwner, { result ->
            if (result.isSuccess) {
                val weatherDetails = result.getOrNull()
                temperature.text = weatherDetails?.temperature.toString()
                weatherIcon.loadImage(requireContext(), weatherDetails?.icon)
                weatherBrief.text = weatherDetails?.generalInfo
                humidity.text = weatherDetails?.humidity.toString()
                wind.text = weatherDetails?.windSpeed.toString()
                pressure.text = weatherDetails?.airPressure.toString()
                setWeatherImage(weatherDetails?.generalInfo, binding)
                progressBar.visibility = View.INVISIBLE
                weatherDetailsScreen.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.INVISIBLE
                textViewError.text = result.exceptionOrNull()?.message
                textViewError.visibility = View.VISIBLE
            }
        })
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
}