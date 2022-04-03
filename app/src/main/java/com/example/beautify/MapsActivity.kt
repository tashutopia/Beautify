package com.example.beautify

//import com.google.maps.example.R

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.graphics.Color.argb
import android.graphics.Color.rgb
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.internal.Constants
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*


class MapsActivity : AppCompatActivity(), OnMyLocationButtonClickListener,
    OnMyLocationClickListener, OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var beau = argb(75,46,148,38)
    lateinit var geofencingClient: GeofencingClient
    lateinit var currPos: Location
    private var payable :Boolean = false
    private val circleList = mutableListOf<Circle>()
    private var nearCirc: Circle? = null

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("MissingPermission")
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
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geofencingClient = LocationServices.getGeofencingClient(this)


        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                camReBound(LatLng(location!!.latitude, location.longitude))
            }
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                p0 ?: return
                for (location in p0.locations) {
                    camReBound(LatLng(location!!.latitude, location.longitude))
                    currPos = location

                    nearCirc = circleList[nearestCircle()]

                    payable = inRange(LatLng(location.latitude, location.longitude), nearCirc!!.center)
                }
            }

        }
        startLocationUpdates()
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

        mMap.setMinZoomPreference(14.0f)
        mMap.setMaxZoomPreference(16.0f)
        // Add a marker in Sydney and move the camera
        addMarker("GaTech",33.775778305161886, -84.39633864568813)
        addArea(LatLng(33.775778305161886, -84.39633864568813),"GaTech")

        mMap.isMyLocationEnabled = true
        mMap.setOnMyLocationButtonClickListener(this)
        mMap.setOnMyLocationClickListener(this)


    }
    private val locationRequest = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
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
    @RequiresApi(Build.VERSION_CODES.N)
    private val locationPermissionRequest = registerForActivityResult(
        RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
            } else -> {
            // No location access granted.
        }
        }
    }

    private var requestingLocationUpdates :Boolean = true

    override fun onResume() {
        super.onResume()

        if (requestingLocationUpdates) startLocationUpdates()
    }



    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.getMainLooper())
    }
    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }



    private fun addArea(latLng: LatLng, name:String) {

        val circle: Circle = mMap.addCircle(
            CircleOptions()
                .center(latLng)
                .radius(150.0)
                .strokeColor(Color.BLACK)
                .fillColor(beau)
                .clickable(true)
        )
        circle.tag = name
        circleList.add(circle)
    /**
        geofenceList.add(Geofence.Builder()
            .setRequestId(name)
            .setCircularRegion(
                latLng.latitude,
                latLng.longitude,
                1000.0F
            )
            .setExpirationDuration(2000000)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .build())*/
    }

    private fun checkDistTo(latLng1: LatLng,latLng: LatLng) : Float{
        val arr2 = FloatArray(1)
        Location.distanceBetween(latLng1.latitude,latLng1.longitude,latLng.latitude,latLng.longitude,arr2)
        return arr2[0]
    }

    private fun inRange(latLng1: LatLng,latLng: LatLng) : Boolean{
        val arr2 = FloatArray(1)
        Location.distanceBetween(latLng1.latitude,latLng1.longitude,latLng.latitude,latLng.longitude,arr2)
        return arr2[0] <= 150
    }

    private fun nearestCircle():Int {
        var mindex = 0
        var mistance = 1000000
        var i = 0
        for (circle in circleList) {

            var distance = checkDistTo(LatLng(currPos.latitude,currPos.longitude),circle.center)
            if(distance < mistance) {
                mindex =  i
            }
            i++
        }

        return mindex

    }


}





