package com.edwin.weatherapp.presentation.weatherDetails

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.edwin.domain.exception.WeatherException
import com.edwin.domain.model.BriefWeatherInfo
import com.edwin.weatherapp.R
import com.edwin.weatherapp.databinding.WeatherDetailsFragmentBinding
import com.edwin.weatherapp.util.loadImage
import com.edwin.weatherapp.util.showSnackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class WeatherDetailsFragment : Fragment(R.layout.weather_details_fragment) {

    private val viewModel: WeatherDetailsViewModel by viewModel()
    private val args: WeatherDetailsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = WeatherDetailsFragmentBinding.bind(view)
        weatherScreenSetup(binding)
        binding.retryButton.setOnClickListener {
            fetchWeatherInfo(binding)
        }
        setHasOptionsMenu(true)
    }

    private fun fetchWeatherInfo(binding: WeatherDetailsFragmentBinding) = with(binding) {
        textViewError.visibility = View.INVISIBLE
        retryButton.visibility = View.INVISIBLE
        progressBar.visibility = View.VISIBLE
        viewModel.getWeatherDetails(args.latitude, args.longitude)
    }

    private fun weatherScreenSetup(binding: WeatherDetailsFragmentBinding) = with(binding) {
        fetchWeatherInfo(binding)
        viewModel.weatherDetails.observe(viewLifecycleOwner, { result ->
            result.onSuccess { weatherDetails ->
                progressBar.visibility = View.INVISIBLE
                temperature.text = weatherDetails?.temperature.toString()
                weatherIcon.loadImage(requireContext(), weatherDetails?.icon)
                weatherBrief.text = weatherDetails?.generalInfo
                humidity.text = getString(R.string.humidity_value, weatherDetails?.humidity)
                wind.text = getString(
                    R.string.wind_value,
                    weatherDetails?.windDirection?.direction,
                    weatherDetails?.windSpeed
                )
                pressure.text = getString(R.string.pressure_value, weatherDetails?.pressure)
                setWeatherImage(weatherDetails?.briefWeatherInfo, binding)
                weatherDetailsScreen.visibility = View.VISIBLE
            }
            result.onFailure { exception ->
                when (exception) {
                    is WeatherException.HostFailure -> {
                        progressBar.visibility = View.INVISIBLE
                        textViewError.text = getString(R.string.host_failure_error_text)
                        textViewError.visibility = View.VISIBLE
                        retryButton.visibility = View.VISIBLE
                    }
                    is WeatherException.NoWeatherData -> {
                        progressBar.visibility = View.INVISIBLE
                        textViewError.text = getString(R.string.no_weather_data_error_text)
                        textViewError.visibility = View.VISIBLE
                        retryButton.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun setWeatherImage(
        briefWeatherInfo: BriefWeatherInfo?,
        binding: WeatherDetailsFragmentBinding
    ) {
        when (briefWeatherInfo) {
            BriefWeatherInfo.SCATTERED_CLOUDS -> binding.weatherImage.setImageResource(R.drawable.scattered_clouds)
            BriefWeatherInfo.CLEAR_SKY -> binding.weatherImage.setImageResource(R.drawable.clear_sky)
            BriefWeatherInfo.RAIN -> binding.weatherImage.setImageResource(R.drawable.rain)
            BriefWeatherInfo.THUNDERSTORM -> binding.weatherImage.setImageResource(R.drawable.thunderstorm)
            BriefWeatherInfo.FEW_CLOUDS -> binding.weatherImage.setImageResource(R.drawable.few_clouds)
            BriefWeatherInfo.BROKEN_CLOUDS -> binding.weatherImage.setImageResource(R.drawable.broken_clouds)
            BriefWeatherInfo.SHOWER_RAIN -> binding.weatherImage.setImageResource(R.drawable.shower_rain)
            BriefWeatherInfo.SNOW -> binding.weatherImage.setImageResource(R.drawable.snow)
            BriefWeatherInfo.MIST -> binding.weatherImage.setImageResource(R.drawable.mist)
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
                showSnackbar(getString(R.string.share_toast_text))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}