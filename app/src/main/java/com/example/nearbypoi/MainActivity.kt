package com.example.nearbypoi

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.nearbypoi.ui.theme.NearbyPOITheme
import com.example.nearbypoi.view.NearbyScreen
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var locationCallback: LocationCallback? = null
    var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationRequired = false
    override fun onCreate(savedInstanceState: Bundle?) {
        mainActivity = this
        super.onCreate(savedInstanceState)
        setContent {
            NearbyPOITheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current
                    var currentLocation by remember {
                        mutableStateOf(LatLng(0.0, 0.0))
                    }
                    val launcherMultiplePermissions = rememberLauncherForActivityResult(
                        ActivityResultContracts.RequestMultiplePermissions()
                    ) { permissionsMap ->
                        val areGranted = permissionsMap.values.reduce { acc, next -> acc && next }
                        if (areGranted) {
                            locationRequired = true
                            startLocationUpdates(context)
                            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
                        }
                    }

                    val permissions = arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                    LaunchedEffect(Unit) {
                        if (permissions.all {
                                ContextCompat.checkSelfPermission(
                                    context,
                                    it
                                ) == PackageManager.PERMISSION_GRANTED
                            }) {
                            startLocationUpdates(context)
                        } else {
                            launcherMultiplePermissions.launch(permissions)
                        }
                        fusedLocationClient =
                            LocationServices.getFusedLocationProviderClient(context)
                        locationCallback = object : LocationCallback() {
                            override fun onLocationResult(p0: LocationResult) {
                                for (lo in p0.locations) {
                                    currentLocation = LatLng(lo.latitude, lo.longitude)
                                    val pref =
                                        context.getSharedPreferences("my_shared", MODE_PRIVATE)
                                    pref.edit().putFloat("lat", lo.latitude.toFloat()).apply()
                                    pref.edit().putFloat("long", lo.longitude.toFloat()).apply()
                                }
                            }
                        }
                    }
                    if (permissions.all {
                            ContextCompat.checkSelfPermission(
                                context,
                                it
                            ) == PackageManager.PERMISSION_GRANTED
                        }) {
                        NearbyScreen(modifier = Modifier)
                    }
                }
            }
        }
    }

    private fun startLocationUpdates(context: Context) {
        locationCallback?.let {
            val locationRequest =
                LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 10000).build()
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            fusedLocationClient?.requestLocationUpdates(
                locationRequest,
                it,
                Looper.getMainLooper()
            )
        }
    }


    override fun onResume() {
        super.onResume()
        if (locationRequired) {
            startLocationUpdates(this)
        }
    }

    override fun onPause() {
        super.onPause()
        locationCallback?.let { fusedLocationClient?.removeLocationUpdates(it) }
    }


    companion object {
        var mainActivity: MainActivity? = null

        fun getInstance(): MainActivity? = mainActivity
    }
}