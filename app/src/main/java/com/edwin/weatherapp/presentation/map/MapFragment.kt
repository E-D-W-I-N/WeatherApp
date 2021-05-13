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
import androidx.navigation.fragment.findNavController
import com.edwin.domain.exception.MapException
import com.edwin.weatherapp.R
import com.edwin.weatherapp.databinding.MapFragmentBinding
import com.edwin.weatherapp.extentions.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
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
            //Если разрешение выдано показываем Snackbar и выполняем действие
            showSnackbar(getString(R.string.permission_granted))
            moveCameraToCurrentLocation()
        } else {
            showSnackbar(getString(R.string.permission_denied))
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
            map.addMarker {
                position(latLng)
            }
            // Если текущий зум камеры выше стандартного, то не уменьшаем его
            val cameraPosition = if (googleMap.cameraPosition.zoom > ON_CLICK_MAP_ZOOM) {
                CameraUpdateFactory.newLatLng(latLng)
            } else {
                CameraUpdateFactory.newLatLngZoom(latLng, ON_CLICK_MAP_ZOOM)
            }
            map.animateCamera(cameraPosition)
            // Делаем progressBar видимым и обновляем Address во ViewModel
            binding.progressBar.visibility = View.VISIBLE
            viewModel.getAddress(latLng.latitude, latLng.longitude)
        }
        viewModel.address.observe(viewLifecycleOwner, { result ->
            // При успешном получении Address убираем progressBar и показываем окно
            result.onSuccess { address ->
                binding.progressBar.visibility = View.INVISIBLE
                setupShowWeatherWindow(address)
            }
            // При неудачном получении Address убираем progressBar и обрабатываем ошибки
            result.onFailure { exception ->
                binding.progressBar.visibility = View.INVISIBLE
                when (exception) {
                    is MapException.GeocoderFailed -> showSnackbar(
                        getString(R.string.check_connection_error_text)
                    )
                    is MapException.CityNotFound -> showSnackbar(
                        getString(R.string.no_city_error_text)
                    )
                }
            }
        })
    }

    private fun setupShowWeatherWindow(address: Address) = with(binding) {
        cityName.text = address.locality
        cityLatlng.text = getString(R.string.cityLatlng, address.latitude, address.longitude)
        // По клику на кнопку закрытия окна скрываем его с анимацией
        closeWindowButton.setOnClickListener {
            showWeatherWindow.animateOut()
        }
        /* По клику на кнопку "Show weather" открываем WeatherDetailsFragment.
        Через SafeArgs передаем в него название города и его координаты */
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
            // Если пользователь отказался давать разрешение, то пытаемся убедить его :)
            shouldShowRequestPermissionRationale(LOCATION_PERMISSION) -> {
                showDialog()
            }
            else -> requestPermissionLauncher.launch(LOCATION_PERMISSION)
        }
    }

    private fun moveCameraToCurrentLocation() {
        binding.progressBar.visibility = View.VISIBLE
        // Обновляем значение LiveData во ViewModel
        viewModel.getFusedLocation()
        viewModel.fusedLocation.observe(viewLifecycleOwner, { result ->
            // Если локация пользователя получена успешно, то приближаем камеру и ставим маркер
            result.onSuccess { location ->
                binding.progressBar.visibility = View.INVISIBLE
                val latLng = LatLng(location.latitude, location.longitude)
                val cameraPosition = CameraUpdateFactory.newLatLngZoom(latLng, ON_START_MAP_ZOOM)
                map.animateCamera(cameraPosition)
                map.addMarker {
                    position(latLng)
                    title(getString(R.string.marker_title))
                    icon(requireContext(), R.drawable.ic_my_location)
                }
            }
            // Если при получении локации возникли исключения, то обрабатываем их
            result.onFailure { exception ->
                binding.progressBar.visibility = View.INVISIBLE
                when (exception) {
                    is MapException.NoLastLocation -> {
                        showSnackbar(getString(R.string.current_location_error_text)) {
                            action(R.string.action_retry) {
                                binding.progressBar.visibility = View.VISIBLE
                                viewModel.getFusedLocation()
                            }
                        }
                    }
                }
            }
        })
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
            // При клике на иконку поиска в меню показываем Snackbar
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

}