package com.example.nearbypoi.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nearbypoi.model.PlacePhoto
import com.example.nearbypoi.model.QueryDetail
import com.example.nearbypoi.model.place_details.PlaceDetail
import com.example.nearbypoi.repository.NearbyRepository
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NearbyViewModel @Inject constructor(val repository: NearbyRepository) : ViewModel() {
    private val _poi: MutableStateFlow<PointOfInterest?> = MutableStateFlow(
        null
    )
    val poi: StateFlow<PointOfInterest?> get() = _poi

    private val _placeDetail: MutableStateFlow<PlaceDetail?> = MutableStateFlow(
        null
    )
    val placeDetail: StateFlow<PlaceDetail?> get() = _placeDetail

    private val _queryString: MutableStateFlow<String?> = MutableStateFlow(
        null
    )
    val queryString: StateFlow<String?> get() = _queryString

    private val _query: MutableStateFlow<QueryDetail?> = MutableStateFlow(
        null
    )
    val query: StateFlow<QueryDetail?> get() = _query

    private val _latLng: MutableStateFlow<LatLng?> = MutableStateFlow(
        null
    )
    val latLng: StateFlow<LatLng?> get() = _latLng



    fun getPlaceDetails(placeId: String) {
        viewModelScope.launch {
            val placeDetail = async {
                repository.placeDetailsJson(placeId)
            }.await()

            if (placeDetail.isSuccessful) {
                _placeDetail.value = placeDetail.body()
            }
        }
    }

    fun queryAutocomplete() {
        viewModelScope.launch {
            val query = async {
                repository.queryAutocompleteJson(
                    queryString.value!!,
                    "${latLng.value?.latitude},${latLng.value?.longitude}"
                )
            }.await()

            if (query.isSuccessful) {
                _query.value = query.body()
            }
        }
    }

    fun onPOIChanged(poi: PointOfInterest) {
        _poi.value = poi
        getPlaceDetails(poi.placeId)
    }

    fun onLatLngChanged(latLng:LatLng){
        _latLng.value=latLng
    }

    fun onQueryStringChanged(queryString: String) {
        _queryString.value = queryString
        queryAutocomplete()
    }

}