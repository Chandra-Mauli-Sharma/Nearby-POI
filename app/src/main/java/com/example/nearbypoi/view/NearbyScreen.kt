package com.example.nearbypoi.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.nearbypoi.BuildConfig
import com.example.nearbypoi.ui.theme.Orange
import com.example.nearbypoi.util.Constants
import com.example.nearbypoi.viewmodel.NearbyViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

private var locationCallback: LocationCallback? = null
var fusedLocationClient: FusedLocationProviderClient? = null

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyScreen(
    modifier: Modifier,
    viewModel: NearbyViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val permissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    var isLocRequired by remember {
        mutableStateOf(permissions.all {
            ContextCompat.checkSelfPermission(
                context,
                it
            ) == PackageManager.PERMISSION_GRANTED
        })
    }

    var uiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = isLocRequired
            )
        )
    }


    val modalSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
        confirmValueChange = { it != SheetValue.PartiallyExpanded },
    )

    var properties by remember {
        mutableStateOf(
            MapProperties(
                mapType = MapType.NORMAL,
                isIndoorEnabled = true,
                isBuildingEnabled = true,
                isMyLocationEnabled = isLocRequired
            )
        )
    }

    val coroutine = rememberCoroutineScope()
    val cameraPositionState = rememberCameraPositionState()
    val placeDetail by viewModel.placeDetail.collectAsState()
    val bottomSheetScaffoldState =
        rememberBottomSheetScaffoldState(bottomSheetState = modalSheetState)
    val queryString by viewModel.queryString.collectAsState()
    val query by viewModel.query.collectAsState()
    var searchSelected by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = Unit, block = {
        val pref =
            context.getSharedPreferences("my_shared", ComponentActivity.MODE_PRIVATE)
    })

    var currentLocation by remember {
        mutableStateOf(LatLng(0.0, 0.0))
    }
    val launcherMultiplePermissions = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        val areGranted = permissionsMap.values.reduce { acc, next -> acc && next }
        if (areGranted) {
            startLocationUpdates(context)
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }
    LaunchedEffect(isLocRequired) {
        if (isLocRequired) {
            if (permissions.all {
                    ContextCompat.checkSelfPermission(
                        context,
                        it
                    ) == PackageManager.PERMISSION_GRANTED
                }) {
                startLocationUpdates(context)
            } else {
                launcherMultiplePermissions.launch(permissions)
                isLocRequired = true
                uiSettings=uiSettings.copy(myLocationButtonEnabled = true)
                properties=properties.copy(isMyLocationEnabled = true)

            }
            fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(context)
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    for (lo in p0.locations) {
                        currentLocation = LatLng(lo.latitude, lo.longitude)
                        val pref =
                            context.getSharedPreferences(
                                "my_shared",
                                ComponentActivity.MODE_PRIVATE
                            )
                        pref.edit().putFloat("lat", lo.latitude.toFloat()).apply()
                        pref.edit().putFloat("long", lo.longitude.toFloat()).apply()
                    }
                }
            }
        }
    }

    BottomSheetScaffold(
        sheetContent = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (bottomSheetScaffoldState.bottomSheetState.isVisible && placeDetail != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("${Constants.api_base_url}photo?maxwidth=400&photo_reference=${placeDetail?.result?.photos?.first()?.photoReference}&key=${BuildConfig.MAPS_API_KEY}")
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                    Text(
                        modifier = modifier.padding(10.dp),
                        text = placeDetail?.result?.name!!,
                        style = MaterialTheme.typography.displaySmall
                    )
                    Text(
                        modifier = modifier.padding(10.dp),
                        text = placeDetail?.result?.vicinity!!,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Row(
                        modifier
                            .border(
                                width = 1.dp,
                                color = Color.Black,
                                shape = RoundedCornerShape(5.dp)
                            )
                            .padding(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = null)
                        Text(
                            text = placeDetail?.result?.rating.toString(),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                    Box(modifier = modifier.height(10.dp))
                }
            }
        },
        scaffoldState = bottomSheetScaffoldState,
        sheetPeekHeight = 0.dp,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = modifier.fillMaxWidth()
            ) {
                this@Column.AnimatedVisibility(visible = searchSelected) {
                    IconButton(onClick = { searchSelected = !searchSelected }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null)
                    }
                }
                OutlinedTextField(
                    value = queryString ?: "",
                    label = { Text(text = "Search") },
                    onValueChange = viewModel::onQueryStringChanged,
                    enabled = searchSelected,
                    modifier = modifier.clickable { searchSelected = !searchSelected },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Orange,
                        focusedLabelColor = Orange,
                        cursorColor = Orange
                    )
                )
                IconButton(
                    onClick = { isLocRequired = true },
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = Orange)
                ) {
                    Icon(
                        imageVector = if (isLocRequired) Icons.Default.Search else Icons.Default.MyLocation,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
            Box {
                GoogleMap(
                    modifier = modifier
                        .padding(it)
                        .padding(20.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    uiSettings = uiSettings,
                    properties = properties,
                    cameraPositionState = cameraPositionState,
                    onPOIClick = { pointOfInterest: PointOfInterest ->
                        coroutine.launch {
                            bottomSheetScaffoldState.bottomSheetState.expand()
                        }
                        viewModel.onPOIChanged(pointOfInterest)
                    }
                )
                this@Column.AnimatedVisibility(visible = searchSelected) {
                    LazyColumn(
                        modifier
                            .background(color = Color.White)
                            .fillMaxSize()
                    ) {
                        items(query?.predictions ?: listOf()) {
                            Row(
                                modifier
                                    .fillMaxWidth()
                                    .padding(20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowOutward,
                                    contentDescription = null
                                )
                                Text(it.description)
                            }
                        }
                    }
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