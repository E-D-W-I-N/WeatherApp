package com.edwin.weatherapp.feature.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.edwin.weatherapp.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.koin.androidx.viewmodel.ext.android.viewModel

class MapFragment : Fragment(), OnMapReadyCallback {

    private val viewModel: MapViewModel by viewModel()
    private lateinit var mMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.map_fragment, container, false)
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        return rootView
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        viewModel.weatherDetails.observe(viewLifecycleOwner, { result ->
            val weather = result.getOrThrow()
            if (result.isSuccess) {
                Toast.makeText(
                    context,
                    weather?.temperature.toString() + "success",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(context, weather?.temperature.toString() + "fail", Toast.LENGTH_LONG)
                    .show()
            }
        })

        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}