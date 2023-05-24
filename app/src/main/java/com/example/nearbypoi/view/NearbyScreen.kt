package com.example.nearbypoi.view

import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.nearbypoi.ui.theme.Orange
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyScreen(modifier: Modifier) {
    var uiSettings by remember { mutableStateOf(MapUiSettings()) }
    uiSettings = uiSettings.copy(zoomControlsEnabled = false, myLocationButtonEnabled = true)

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
                isMyLocationEnabled = true
            )
        )
    }

    val coroutine = rememberCoroutineScope()
    val contex = LocalContext.current

    var cameraPositionState = rememberCameraPositionState()

    var bottomSheetScaffoldState =
        rememberBottomSheetScaffoldState(bottomSheetState = modalSheetState)
    BottomSheetScaffold(
        sheetContent = { Text(text = "HHey") },
        scaffoldState = bottomSheetScaffoldState,
        sheetPeekHeight = 0.dp,
    ) {
        GoogleMap(
            modifier = modifier.padding(it),
            uiSettings = uiSettings,
            properties = properties,
            cameraPositionState = cameraPositionState,
            onPOIClick = { pointOfInterest: PointOfInterest -> coroutine.launch { bottomSheetScaffoldState.bottomSheetState.expand() } }
        ) {


        }
    }


}