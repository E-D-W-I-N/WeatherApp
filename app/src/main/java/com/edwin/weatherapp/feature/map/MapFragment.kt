package com.edwin.weatherapp.feature.map

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.edwin.weatherapp.R
import com.edwin.weatherapp.databinding.MapFragmentBinding
import com.edwin.weatherapp.util.animateIn
import com.edwin.weatherapp.util.animateOut
import com.edwin.weatherapp.util.icon
import com.edwin.weatherapp.util.showToast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
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
            showToast(getString(R.string.permission_granted), Toast.LENGTH_LONG)
            moveCameraToCurrentLocation()
        } else {
            showToast(getString(R.string.permission_denied), Toast.LENGTH_LONG)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = MapFragmentBinding.bind(view)
        binding.apply {
            mapView.onCreate(savedInstanceState)
            mapView.getMapAsync(this@MapFragment)

            closeWindowButton.setOnClickListener {
                showWeatherWindow.animateOut()
            }
            showWeatherButton.setOnClickListener {
                val action = MapFragmentDirections.actionMapFragmentToWeatherDetailsFragment(
                    cityName.text.toString()
                )
                findNavController().navigate(action)
            }
        }
        setHasOptionsMenu(true)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        checkPermissions()
        val geoCoder = Geocoder(requireContext())
        map.setOnMapClickListener { latLng ->
            map.clear()
            map.addMarker(MarkerOptions().position(latLng))
            val cameraPosition = CameraUpdateFactory.newLatLngZoom(latLng, ON_CLICK_MAP_ZOOM)
            map.animateCamera(cameraPosition)
            val address = geoCoder.getFromLocation(
                latLng.latitude,
                latLng.longitude,
                1
            ).first()
            if (!address.locality.isNullOrBlank()) {
                binding.apply {
                    showWeatherWindow.animateIn()
                    cityName.text = address.locality
                    cityLatlng.text = getString(
                        R.string.cityLatlng,
                        address.latitude,
                        address.longitude
                    )
                }
            }
        }
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
        viewModel.fusedLocation.observe(viewLifecycleOwner, { result ->
            val resultLocation = result.getOrDefault(Location(LocationManager.GPS_PROVIDER))
            val latLng = LatLng(resultLocation.latitude, resultLocation.longitude)
            val cameraPosition = CameraUpdateFactory.newLatLngZoom(latLng, ON_START_MAP_ZOOM)
            map.animateCamera(cameraPosition)
            map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("You're here")
                    .icon(requireContext(), R.drawable.ic_my_location)
            )
        })
    }

    private fun showDialog() {
        AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
            .setMessage(getString(R.string.permission_dialog_message))
            .setTitle(getString(R.string.permission_dialog_title))
            .setPositiveButton(getString(R.string.positive_button_text)) { _, _ ->
                requestPermissionLauncher.launch(LOCATION_PERMISSION)
            }
            .setNegativeButton(getString(R.string.negative_button_text)) { _, _ -> }
            .create().show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_map, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                showToast(getString(R.string.search_toast_text), Toast.LENGTH_LONG)
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

    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapView.onDestroy()
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