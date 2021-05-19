package com.edwin.weatherapp.presentation.map

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.edwin.domain.exception.MapException
import com.edwin.weatherapp.R
import com.edwin.weatherapp.databinding.MapFragmentBinding
import com.edwin.weatherapp.extensions.*
import com.edwin.weatherapp.presentation.map.model.MapAction
import com.edwin.weatherapp.presentation.map.model.MapEvent
import com.edwin.weatherapp.presentation.map.model.MapViewState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class MapFragment : Fragment(R.layout.map_fragment), OnMapReadyCallback {

    private val viewModel: MapViewModel by viewModel()
    private val binding by viewBinding(MapFragmentBinding::bind)
    private lateinit var map: GoogleMap
    private var isLocationNotChecked = true

    companion object {
        private const val LOCATION_PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION
        private const val MAP_ZOOM = 8F
        private const val IS_LOCATION_NOT_CHECKED = "isLocationNotChecked"
    }

    private val requestPermissionLauncher = registerForActivityResult(
        RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            viewModel.obtainEvent(MapEvent.GetFusedLocation)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedInstanceState?.let {
            isLocationNotChecked = it.getBoolean(IS_LOCATION_NOT_CHECKED)
        }
        binding.apply {
            mapView.onCreate(savedInstanceState)
            mapView.getMapAsync(this@MapFragment)
        }
        setHasOptionsMenu(true)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        val viewStates = viewModel.viewStates.flowWithLifecycle(lifecycle)
        viewStates.onEach { bindViewState(it) }.launchIn(viewLifecycleOwner.lifecycleScope)
        val viewActions = viewModel.viewActions.flowWithLifecycle(lifecycle)
        viewActions.onEach { bindViewAction(it) }.launchIn(viewLifecycleOwner.lifecycleScope)

        if (isLocationNotChecked) {
            checkPermissions()
            isLocationNotChecked = false
        }
        map.setOnMapClickListener { latLng ->
            viewModel.obtainEvent(MapEvent.SetClickPosition(latLng))
            viewModel.obtainEvent(MapEvent.GetCityName(latLng.latitude, latLng.longitude))
        }
    }

    private fun bindViewState(viewState: MapViewState) = with(binding) {
        progressBar.isVisible = viewState.isLoading
        when {
            viewState.fusedLocation != null -> {
                val latLng = LatLng(
                    viewState.fusedLocation.latitude,
                    viewState.fusedLocation.longitude
                )
                moveCameraToLocation(latLng)
                viewModel.obtainEvent(MapEvent.GetCityName(latLng.latitude, latLng.longitude))
            }
            viewState.cityName != null && viewState.clickPosition != null -> {
                setupShowWeatherWindow(viewState.cityName, viewState.clickPosition)
            }
            viewState.cityName == null && viewState.clickPosition != null -> {
                if (showWeatherWindow.isVisible) {
                    showWeatherWindow.animateOut()
                }
                moveCameraToLocation(viewState.clickPosition)
            }
            else -> Unit
        }
    }

    private fun bindViewAction(viewAction: MapAction) {
        when (viewAction) {
            is MapAction.ShowError -> {
                when (viewAction.throwable) {
                    is MapException.GeocoderFailed -> showSnackbar(
                        getString(R.string.check_connection_error_text)
                    )
                    is MapException.CityNotFound -> showSnackbar(
                        getString(R.string.no_city_error_text)
                    )
                    is MapException.NoLastLocation -> {
                        binding.banner.showBanner(
                            message = R.string.current_location_error_text,
                            icon = R.drawable.ic_location_off,
                            leftBtnText = R.string.action_dismiss,
                            rightBtnText = R.string.action_retry,
                            leftButtonAction = { binding.banner.dismiss() },
                            rightButtonAction = { viewModel.obtainEvent(MapEvent.GetFusedLocation) }
                        )
                    }
                }
            }
        }
    }

    private fun moveCameraToLocation(latLng: LatLng) {
        val cameraPosition = if (map.cameraPosition.zoom > MAP_ZOOM) {
            CameraUpdateFactory.newLatLng(latLng)
        } else {
            CameraUpdateFactory.newLatLngZoom(latLng, MAP_ZOOM)
        }
        map.clear()
        map.addMarker {
            position(latLng)
        }
        map.animateCamera(cameraPosition)
    }

    private fun setupShowWeatherWindow(cityName: String, clickPosition: LatLng) = with(binding) {
        moveCameraToLocation(clickPosition)
        this.cityName.text = cityName
        cityLatlng.text = getString(
            R.string.cityLatlng,
            clickPosition.latitude,
            clickPosition.longitude
        )
        closeWindowButton.setOnClickListener { showWeatherWindow.animateOut() }
        showWeatherButton.setOnClickListener {
            val action = MapFragmentDirections.actionMapFragmentToWeatherDetailsFragment(
                cityName, clickPosition.latitude.toFloat(), clickPosition.longitude.toFloat()
            )
            findNavController().navigate(action)
        }
        showWeatherWindow.animateIn()
    }

    private fun checkPermissions() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                LOCATION_PERMISSION
            ) == PackageManager.PERMISSION_GRANTED -> {
                viewModel.obtainEvent(MapEvent.GetFusedLocation)
            }
            shouldShowRequestPermissionRationale(LOCATION_PERMISSION) -> {
                showDialog()
            }
            else -> requestPermissionLauncher.launch(LOCATION_PERMISSION)
        }
    }

    private fun showDialog() {
        AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
            .setMessage(getString(R.string.permission_dialog_message))
            .setTitle(getString(R.string.permission_dialog_title))
            .setPositiveButton(getString(android.R.string.ok)) { _, _ ->
                requestPermissionLauncher.launch(LOCATION_PERMISSION)
            }
            .setNegativeButton(getString(android.R.string.cancel)) { _, _ -> }
            .create().show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_map, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                showSnackbar(getString(R.string.search_toast_text))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(IS_LOCATION_NOT_CHECKED, false)
        super.onSaveInstanceState(outState)
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapView.onDestroy()
    }

}