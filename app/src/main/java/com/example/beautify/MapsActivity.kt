package com.example.beautify

//import com.google.maps.example.R

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions


class MapsActivity : AppCompatActivity(), OnMyLocationButtonClickListener,
    OnMyLocationClickListener, OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    var PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val fab: View = findViewById(R.id.camButton)
        fab.setOnClickListener {
            val intent = Intent(this, CameraIn::class.java).apply {

            }
            startActivity(intent)
        }
        val fab2: View = findViewById(R.id.shop_button)
        fab2.setOnClickListener {
            val intent = Intent(this, ShopActivity::class.java).apply {

            }
            startActivity(intent)
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        getLocationPermission()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isRotateGesturesEnabled = false;
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        mMap.setMinZoomPreference(10.0f)
        mMap.setMaxZoomPreference(16.0f)
        // Add a marker in Sydney and move the camera
        addMarker("GaTech",33.775778305161886, -84.39633864568813)


        mMap.isMyLocationEnabled = true
        mMap.setOnMyLocationButtonClickListener(this)
        mMap.setOnMyLocationClickListener(this)


        camReBound(LatLng(33.7700064482008, -84.38588745227676))

    }

    override fun onMyLocationClick(location: Location) {
        Toast.makeText(this, "Current location:\n$location", Toast.LENGTH_LONG)
            .show()
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT)
            .show()
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false
    }

    private fun addMarker(name:String, lat:Double, log:Double) {
        val ref = LatLng(lat, log)
        mMap.addMarker(MarkerOptions()
            .position(ref)
            .title("Marker in $name"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ref))
    }
    private fun camReBound(latLng : LatLng) {
        val builder = LatLngBounds.builder()
        val bounds = builder.include(latLng).build()
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,300,300, 30))
    }
    private fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            var locationPermissionGranted = true
        } else {

            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }
}





