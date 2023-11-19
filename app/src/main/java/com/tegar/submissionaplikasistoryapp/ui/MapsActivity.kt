package com.tegar.submissionaplikasistoryapp.ui

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.tegar.submissionaplikasistoryapp.R
import com.tegar.submissionaplikasistoryapp.data.remote.ResultMessage
import com.tegar.submissionaplikasistoryapp.data.remote.response.ResponseGetAll
import com.tegar.submissionaplikasistoryapp.databinding.ActivityMapsBinding
import com.tegar.submissionaplikasistoryapp.ui.viewmodel.MainViewModelFactory
import com.tegar.submissionaplikasistoryapp.ui.viewmodel.MapsViewModel
import com.tegar.submissionaplikasistoryapp.util.ToastUtils

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val mapsViewModel: MapsViewModel by viewModels {
        MainViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isZoomGesturesEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true

        mMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style)
        )

        mapsViewModel.getAllStoriesWithLocation().observe(this) { result ->
            handleResult(result)
        }
    }

    private fun handleResult(result: ResultMessage<ResponseGetAll>) {

        when (result) {
            is ResultMessage.Loading -> {
                showLoading(true)
            }

            is ResultMessage.Success -> {
                showLoading(false)
                val stories = result.data
                if (stories.listStory.isEmpty()) {
                    ToastUtils.showToast(this, getString(R.string.empty_story))
                } else {
                    val boundsBuilder = LatLngBounds.builder()
                    val markers = mutableListOf<Marker>()

                    stories.listStory.forEach { story ->
                        val lat: Double = story.lat!!
                        val lon: Double = story.lon!!
                        val latLng = LatLng(lat, lon)

                        val marker = mMap.addMarker(
                            MarkerOptions()
                                .position(latLng)
                                .title(story.name)
                                .snippet(story.description)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        )
                        markers.add(marker!!)
                        boundsBuilder.include(latLng)
                    }

                    if (markers.isNotEmpty()) {
                        val markerWithMostStories = markers.last()
                        val markerLatLng = markerWithMostStories.position
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng, ZOOM_LEVEL))
                    }
                }
            }

            is ResultMessage.Error -> {
                showLoading(false)
                val exception = result.exception
                val errorMessage =
                    exception.message ?: getString(R.string.an_error_occurred_while_fetching_data)
                ToastUtils.showToast(this, errorMessage)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val ZOOM_LEVEL = 4.0F
    }

}