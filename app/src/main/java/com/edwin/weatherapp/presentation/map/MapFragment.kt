package com.edwin.weatherapp.presentation.map

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.edwin.domain.DataResult
import com.edwin.weatherapp.R
import com.edwin.weatherapp.databinding.MapFragmentBinding
import com.edwin.weatherapp.util.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class MapFragment : Fragment(R.layout.map_fragment), OnMapReadyCallback {

    private val viewModel: MapViewModel by viewModel()
    private lateinit var binding: MapFragmentBinding
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
            showSnackbar(getString(R.string.permission_granted)) {
                action(android.R.string.ok) {
                    this.dismiss()
                }
            }
            moveCameraToCurrentLocation()
        } else {
            showSnackbar(getString(R.string.permission_denied)) {
                action(android.R.string.ok) {
                    this.dismiss()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = MapFragmentBinding.bind(view)
        binding.apply {
            mapView.onCreate(savedInstanceState)
            mapView.getMapAsync(this@MapFragment)
        }
        setHasOptionsMenu(true)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        checkPermissions()
        map.setOnMapClickListener { latLng ->
            map.clear()
            map.addMarker(MarkerOptions().position(latLng))
            val cameraPosition = CameraUpdateFactory.newLatLngZoom(latLng, ON_CLICK_MAP_ZOOM)
            map.animateCamera(cameraPosition)
            viewModel.getCityName(latLng.latitude, latLng.longitude)
        }
        val flow = viewModel.cityName.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
        flow.onEach { result ->
            when (result) {
                is DataResult.Success -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    setupShowWeatherWindow(result.data)
                }
                is DataResult.Error -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    showSnackbar(result.errorMessage, Snackbar.LENGTH_SHORT) {
                        action(android.R.string.ok) {
                            this.dismiss()
                        }
                    }
                }
                is DataResult.Loading -> binding.progressBar.visibility = View.VISIBLE
                else -> Unit
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun setupShowWeatherWindow(address: Address) = with(binding) {
        cityName.text = address.locality
        cityLatlng.text = getString(
            R.string.cityLatlng,
            address.latitude,
            address.longitude
        )
        closeWindowButton.setOnClickListener {
            showWeatherWindow.animateOut()
        }
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
                moveCameraToCurrentLocation()
            }
            shouldShowRequestPermissionRationale(LOCATION_PERMISSION) -> {
                showDialog()
            }
            else -> requestPermissionLauncher.launch(LOCATION_PERMISSION)
        }
    }

    private fun moveCameraToCurrentLocation() {
        viewModel.getFusedLocation()
        val flow = viewModel.fusedLocation.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
        flow.onEach { result ->
            when (result) {
                is DataResult.Success -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    val resultLocation = result.data
                    val latLng = LatLng(resultLocation.latitude, resultLocation.longitude)
                    val cameraPosition =
                        CameraUpdateFactory.newLatLngZoom(latLng, ON_START_MAP_ZOOM)
                    map.animateCamera(cameraPosition)
                    map.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title("You're here")
                            .icon(requireContext(), R.drawable.ic_my_location)
                    )
                }
                is DataResult.Error -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    showSnackbar(result.errorMessage) {
                        action(R.string.action_retry) {
                            moveCameraToCurrentLocation()
                        }
                    }
                }
                is DataResult.Loading -> binding.progressBar.visibility = View.VISIBLE
                else -> Unit
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
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
                showSnackbar(getString(R.string.search_toast_text)) {
                    action(android.R.string.ok) {
                        this.dismiss()
                    }
                }
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

}