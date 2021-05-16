package com.edwin.weatherapp.presentation.map

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.edwin.domain.exception.MapException
import com.edwin.weatherapp.R
import com.edwin.weatherapp.databinding.MapFragmentBinding
import com.edwin.weatherapp.extensions.*
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

    companion object {
        private const val LOCATION_PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION
        private const val ON_START_MAP_ZOOM = 4F
        private const val ON_CLICK_MAP_ZOOM = 8F
    }

    private val requestPermissionLauncher = registerForActivityResult(
        RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            viewModel.getFusedLocation()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            mapView.onCreate(savedInstanceState)
            mapView.getMapAsync(this@MapFragment)
        }
        setHasOptionsMenu(true)
    }

    private fun setupObservers() {
        val uiStateFlow = viewModel.uiState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
        uiStateFlow.onEach { state ->
            when (state) {
                is MapViewModel.MapUiState.Loading -> binding.progressBar.visibility = View.VISIBLE
                is MapViewModel.MapUiState.CurrentLocationLoaded -> {
                    binding.progressBar.visibility = View.GONE
                    val latLng = LatLng(state.location.latitude, state.location.longitude)
                    moveCameraToCurrentLocation(latLng)
                }
                is MapViewModel.MapUiState.AddressLoaded -> {
                    binding.progressBar.visibility = View.GONE
                    setupShowWeatherWindow(state.address)
                }
                is MapViewModel.MapUiState.Error -> binding.progressBar.visibility = View.GONE
                else -> Unit
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        val eventsFlow = viewModel.eventsFlow.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
        eventsFlow.onEach { event ->
            when (event) {
                is MapViewModel.ActionState.ShowError -> {
                    when (event.throwable) {
                        is MapException.GeocoderFailed -> showSnackbar(
                            getString(R.string.check_connection_error_text)
                        )
                        is MapException.CityNotFound -> showSnackbar(
                            getString(R.string.no_city_error_text)
                        )
                        is MapException.NoLastLocation -> {
                            binding.banner.showBanner(
                                R.string.current_location_error_text,
                                R.drawable.ic_location_off,
                                R.string.action_dismiss,
                                R.string.action_retry,
                                { binding.banner.dismiss() },
                                { viewModel.getFusedLocation() }
                            )
                        }
                    }
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        setupObservers()
        checkPermissions()
        map.setOnMapClickListener { latLng ->
            map.clear()
            map.addMarker {
                position(latLng)
            }
            val cameraPosition = if (googleMap.cameraPosition.zoom > ON_CLICK_MAP_ZOOM) {
                CameraUpdateFactory.newLatLng(latLng)
            } else {
                CameraUpdateFactory.newLatLngZoom(latLng, ON_CLICK_MAP_ZOOM)
            }
            map.animateCamera(cameraPosition)
            viewModel.getAddress(latLng.latitude, latLng.longitude)
        }
    }

    private fun moveCameraToCurrentLocation(latLng: LatLng) {
        val cameraPosition = CameraUpdateFactory.newLatLngZoom(latLng, ON_START_MAP_ZOOM)
        map.animateCamera(cameraPosition)
        map.addMarker {
            position(latLng)
            title(getString(R.string.marker_title))
            icon(requireContext(), R.drawable.ic_my_location)
        }
    }

    private fun setupShowWeatherWindow(address: Address) = with(binding) {
        cityName.text = address.locality
        cityLatlng.text = getString(R.string.cityLatlng, address.latitude, address.longitude)
        closeWindowButton.setOnClickListener { showWeatherWindow.animateOut() }
        showWeatherButton.setOnClickListener {
            val action = MapFragmentDirections.actionMapFragmentToWeatherDetailsFragment(
                address.locality, address.latitude.toFloat(), address.longitude.toFloat()
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
                viewModel.getFusedLocation()
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