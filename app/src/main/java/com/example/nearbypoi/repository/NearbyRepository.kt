package com.example.nearbypoi.repository

import androidx.compose.ui.geometry.CornerRadius
import com.example.nearbypoi.BuildConfig
import com.example.nearbypoi.R
import com.example.nearbypoi.model.Place
import com.example.nearbypoi.util.Constants
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NearbyRepository {
    @GET("nearbysearch/json")
    fun nearBySearchJson(
        @Query("location") location: String,
        @Query("radius") radius: String,
        @Query("key") api_key: String= API_KEY
    ):Response<Place>

    companion object {
        val API_KEY = BuildConfig.MAPS_API_KEY
    }
}