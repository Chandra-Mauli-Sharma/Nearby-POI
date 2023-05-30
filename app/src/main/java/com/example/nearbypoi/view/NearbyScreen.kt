package com.example.nearbypoi.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideIn
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationDisabled
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.nearbypoi.model.place_details.PlaceDetail
import com.example.nearbypoi.ui.theme.Orange
import com.example.nearbypoi.util.Constants
import com.example.nearbypoi.viewmodel.NearbyViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.PointOfInterest
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.launch

private var locationCallback: LocationCallback? = null
var fusedLocationClient: FusedLocationProviderClient? = null

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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
    val permissionGranted by remember {
        mutableStateOf(permissions.all {
            ContextCompat.checkSelfPermission(
                context,
                it
            ) == PackageManager.PERMISSION_GRANTED
        })
    }


    // Map UI Settings
    val uiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false,
                mapToolbarEnabled = true
            )
        )
    }

    var properties by remember {
        mutableStateOf(
            MapProperties(
                mapType = MapType.NORMAL,
                isIndoorEnabled = true,
                isBuildingEnabled = true,
                isMyLocationEnabled = permissionGranted
            )
        )
    }


    // States
    val modalSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
        confirmValueChange = { it != SheetValue.PartiallyExpanded },
    )
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
    var myPOI by remember {
        mutableStateOf<LatLng?>(null)
    }
    val markerState = rememberMarkerState(
        "POI",
        myPOI?:LatLng(0.0,0.0)
    )
    val currentLocation by viewModel.latLng.collectAsState()
    val launcherMultiplePermissions = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        val areGranted = permissionsMap.values.reduce { acc, next -> acc && next }
        if (areGranted) {
            startLocationUpdates(context)
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
            properties = properties.copy(isMyLocationEnabled = true)
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    // SideEffects
    LaunchedEffect(key1 = myPOI, block = {
        if (myPOI != null)
            coroutine.launch {
                cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(myPOI!!, 15f))
            }
    })

    LaunchedEffect(key1 = Unit, block = {
        val pref =
            context.getSharedPreferences("my_shared", ComponentActivity.MODE_PRIVATE)
        viewModel.onLatLngChanged(
            LatLng(
                pref.getFloat("lat", 0f).toDouble(),
                pref.getFloat("long", 0f).toDouble()
            )
        )
    })

    LaunchedEffect(isLocRequired) {
        if (isLocRequired) {
            if (permissionGranted) {
                startLocationUpdates(context)
            } else {
                launcherMultiplePermissions.launch(permissions)
            }
            fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(context)
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    for (lo in p0.locations) {
                        viewModel.onLatLngChanged(LatLng(lo.latitude, lo.longitude))
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

    // UI
    BottomSheetScaffold(
        sheetContent = {
            BottomSheetContent(
                bottomSheetScaffoldState = bottomSheetScaffoldState,
                placeDetail = placeDetail,
                modifier = modifier
            )
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
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = modifier.fillMaxWidth().padding(5.dp)
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
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    enabled = searchSelected,
                    modifier = modifier
                        .padding(10.dp)
                        .clickable { searchSelected = !searchSelected },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Orange,
                        focusedLabelColor = Orange,
                        cursorColor = Orange
                    )
                )
                IconButton(
                    onClick = {
                        if (permissions.all {
                                ContextCompat.checkSelfPermission(
                                    context,
                                    it
                                ) == PackageManager.PERMISSION_GRANTED
                            })
                        coroutine.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(
                                    currentLocation!!,
                                    15f
                                )
                            )
                        } else isLocRequired = true
                    },
                    modifier = modifier.background(color = Orange, shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
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
                    },
                ) {
                    if (placeDetail != null) {
                        myPOI = LatLng(
                            placeDetail!!.result.geometry.location.lat,
                            placeDetail!!.result.geometry.location.lng
                        )

                        markerState.position= LatLng(myPOI!!.latitude,myPOI!!.longitude)
                        Marker(markerState, onClick = { marker: Marker ->
                            coroutine.launch {
                                bottomSheetScaffoldState.bottomSheetState.expand()
                            }
                            viewModel.onPOIChanged(
                                PointOfInterest(
                                    marker.position,
                                    placeDetail!!.result.placeid,
                                    placeDetail!!.result.name
                                )
                            )
                            return@Marker true
                        })
                    }
                }
                this@Column.AnimatedVisibility(visible = searchSelected) {
                    LazyColumn(
                        modifier
                            .background(color = Color.White)
                            .fillMaxSize()
                    ) {
                        items(query?.predictions ?: listOf()) {
                            TextButton(
                                onClick = {
                                    viewModel.getPlaceDetails(it.placeId)
                                    searchSelected = false
                                    coroutine.launch {
                                        bottomSheetScaffoldState.bottomSheetState.expand()
                                    }
                                },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = Color.White,
                                    containerColor = Orange
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = modifier
                                    .padding(10.dp)

                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowOutward,
                                        contentDescription = null,
                                        tint = Orange,
                                        modifier = modifier
                                            .clip(CircleShape)
                                            .background(Color.White)
                                    )
                                    Box(modifier = modifier.width(10.dp))
                                    Text(it.description)
                                }
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun BottomSheetContent(
    bottomSheetScaffoldState: BottomSheetScaffoldState,
    placeDetail: PlaceDetail?,
    modifier: Modifier
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (bottomSheetScaffoldState.bottomSheetState.isVisible && placeDetail != null) {
            val pagerState = rememberPagerState(
                initialPage = 0,
                initialPageOffsetFraction = 0f
            ) {
                return@rememberPagerState placeDetail?.result?.photos?.size!!
            }
            HorizontalPager(state = pagerState) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(
                            "${Constants.api_base_url}photo?maxwidth=400&photo_reference=${
                                placeDetail?.result?.photos?.get(
                                    it
                                )?.photoReference
                            }&key=${BuildConfig.MAPS_API_KEY}"
                        )
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(20.dp)
                        .clip(RoundedCornerShape(20.dp))
                )
            }
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
                    .background(Orange, RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Color.White)
                Text(
                    text = placeDetail?.result?.rating.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
            }
            Box(modifier = modifier.height(10.dp))
        }
    }
}