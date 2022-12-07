package com.example.storyappsubmission1.Activity.Main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.storyappsubmission1.Activity.Start.WelcomeActivity
import com.example.storyappsubmission1.Data.Functon.Result
import com.example.storyappsubmission1.Data.Response.ListStoryR
import com.example.storyappsubmission1.R
import com.example.storyappsubmission1.ViewModel.FactoryVM
import com.example.storyappsubmission1.ViewModel.MapsVM
import com.example.storyappsubmission1.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var _binding: ActivityMapsBinding
    private val _mapsVM: MapsVM by viewModels {
        FactoryVM.getInstance(this)
    }
    private val boundsBuilder = LatLngBounds.Builder()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        getMyLocation()

        _mapsVM.getToken().observe(this) { token ->
            if (token.isEmpty()) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                _mapsVM.getStoriesWithLocation(token).observe(this) { response ->
                    when (response) {
                        is Result.Loading -> {
                        }

                        is Result.Error -> {
                            Toast.makeText(this@MapsActivity, response.message, Toast.LENGTH_SHORT)
                                .show()
                        }

                        is Result.Success -> {
                            if (response.data != null)
                                addStoriesMarker(response.data.listStory)
                            Toast.makeText(this@MapsActivity, response.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }
    }


    private fun addStoriesMarker(listStory: List<ListStoryR>) {
        listStory.forEach { story ->
            val latLng = LatLng(story.lat, story.lon)
            mMap.addMarker(
                MarkerOptions().position(latLng).title(story.name).icon(
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)
                )
            )
            boundsBuilder.include(latLng)
        }

        val bounds: LatLngBounds = boundsBuilder.build()
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                300
            )
        )
    }

    private fun createMarkerStory(listStory: List<ListStoryR>, mMap: GoogleMap) {
        for (story in listStory) {
            val marker = LatLng(story.lat, story.lon)
            mMap.addMarker(MarkerOptions().position(marker).title("${story.name} Stories"))
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }



    @SuppressLint("MissingPermission")
    private fun getMyLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION)
            )
        }
    }
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                // Precise location access granted.
                getMyLocation()
            }
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                // Only approximate location access granted.
                getMyLocation()
            }
        }
    }
}