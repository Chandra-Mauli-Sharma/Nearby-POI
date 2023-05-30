package com.example.nearbypoi.repository

import androidx.compose.ui.geometry.CornerRadius
import com.example.nearbypoi.BuildConfig
import com.example.nearbypoi.R
import com.example.nearbypoi.model.Place
import com.example.nearbypoi.model.PlacePhoto
import com.example.nearbypoi.model.QueryDetail
import com.example.nearbypoi.model.place_details.PlaceDetail
import com.example.nearbypoi.util.Constants
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.lang.ref.PhantomReference

interface NearbyRepository {
    @GET("nearbysearch/json")
    suspend fun nearBySearchJson(
        @Query("location") location: String,
        @Query("radius") radius: String="10",
        @Query("key") api_key: String= API_KEY
    ):Response<Place>

    @GET("details/json")
    suspend fun placeDetailsJson(
        @Query("place_id") placeId: String,
        @Query("key") api_key: String= API_KEY
    ):Response<PlaceDetail>

    @GET("queryautocomplete/json")
    suspend fun queryAutocompleteJson(
        @Query("input") input: String,
        @Query("location") location: String,
        @Query("radius") radius: String="1000",
        @Query("key") api_key: String= API_KEY
    ):Response<QueryDetail>


    companion object {
        val API_KEY = BuildConfig.MAPS_API_KEY
    }
}