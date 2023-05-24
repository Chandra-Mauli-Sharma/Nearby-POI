package com.example.nearbypoi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nearbypoi.repository.NearbyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NearbyViewModel @Inject constructor(val repository: NearbyRepository):ViewModel() {
    fun getNearbyDetails(){viewModelScope.launch {
        repository.nearBySearchJson()
    }}
}