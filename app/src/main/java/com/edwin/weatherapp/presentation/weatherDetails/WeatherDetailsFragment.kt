package com.edwin.weatherapp.presentation.weatherDetails

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.edwin.domain.exception.WeatherException
import com.edwin.domain.model.BriefWeatherInfo
import com.edwin.domain.model.WeatherDetails
import com.edwin.weatherapp.R
import com.edwin.weatherapp.databinding.WeatherDetailsFragmentBinding
import com.edwin.weatherapp.extensions.loadImage
import com.edwin.weatherapp.extensions.showSnackbar
import com.edwin.weatherapp.presentation.weatherDetails.model.WeatherAction
import com.edwin.weatherapp.presentation.weatherDetails.model.WeatherEvent
import com.edwin.weatherapp.presentation.weatherDetails.model.WeatherViewState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class WeatherDetailsFragment : Fragment(R.layout.weather_details_fragment) {

    private val viewModel: WeatherDetailsViewModel by viewModel()
    private val args: WeatherDetailsFragmentArgs by navArgs()
    private val binding by viewBinding(WeatherDetailsFragmentBinding::bind)
    private var isWeatherNotFetched = true

    companion object {
        private const val IS_WEATHER_NOT_FETCHED = "isWeatherNotFetched"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedInstanceState?.let {
            isWeatherNotFetched = it.getBoolean(IS_WEATHER_NOT_FETCHED)
        }

        val viewStates = viewModel.viewStates.flowWithLifecycle(lifecycle)
        viewStates.onEach { bindViewState(it) }.launchIn(viewLifecycleOwner.lifecycleScope)
        val viewActions = viewModel.viewActions.flowWithLifecycle(lifecycle)
        viewActions.onEach { bindViewAction(it) }.launchIn(viewLifecycleOwner.lifecycleScope)

        if (isWeatherNotFetched) {
            viewModel.obtainEvent(WeatherEvent.FetchWeather(args.latitude, args.longitude))
            isWeatherNotFetched = false
        }

        binding.retryButton.setOnClickListener {
            viewModel.obtainEvent(WeatherEvent.FetchWeather(args.latitude, args.longitude))
        }
        setHasOptionsMenu(true)
    }

    private fun bindViewState(viewState: WeatherViewState) = with(binding) {
        progressBar.isVisible = viewState.isLoading
        textViewError.isVisible = false
        retryButton.isVisible = false
        when {
            viewState.error != null -> {
                handleErrors(viewState.error)
                textViewError.isVisible = true
                retryButton.isVisible = true
            }
            viewState.weatherDetails != null -> {
                setWeatherData(viewState.weatherDetails)
                weatherDetailsScreen.isVisible = true
            }
        }
    }

    private fun bindViewAction(viewAction: WeatherAction) {
        when (viewAction) {
            is WeatherAction.ShowNoImageSnackbar -> {
                showSnackbar(getString(R.string.no_image_error_text))
            }
        }
    }

    private fun handleErrors(exception: Throwable) = with(binding) {
        when (exception) {
            is WeatherException.HostFailure -> textViewError.text =
                getString(R.string.host_failure_error_text)
            is WeatherException.NoWeatherData -> textViewError.text =
                getString(R.string.no_weather_data_error_text)
        }
    }

    private fun setWeatherData(weatherDetails: WeatherDetails?) = with(binding) {
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
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(IS_WEATHER_NOT_FETCHED, isWeatherNotFetched)
        super.onSaveInstanceState(outState)
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