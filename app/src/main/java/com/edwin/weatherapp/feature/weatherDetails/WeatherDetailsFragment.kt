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

        val cityName = args.cityName
        viewModel.getWeatherDetails(cityName).observe(viewLifecycleOwner, { result ->
            val weatherDetails = result.getOrNull()
            binding.apply {
                temperature.text = weatherDetails?.temperature.toString()
                weatherIcon.loadImage(requireContext(), weatherDetails?.icon)
                weatherBrief.text = weatherDetails?.generalInfo
                humidity.text = weatherDetails?.humidity.toString()
                wind.text = weatherDetails?.windSpeed.toString()
                pressure.text = weatherDetails?.airPressure.toString()
            }
        })
    }
}