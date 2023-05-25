package com.example.nearbypoi.model.place_details

import com.example.nearbypoi.model.Geometry
import com.example.nearbypoi.model.Location
import com.example.nearbypoi.model.OpeningHours
import com.example.nearbypoi.model.Photo
import com.example.nearbypoi.model.PlusCode
import com.example.nearbypoi.model.Result
import com.example.nearbypoi.model.Viewport
import com.google.gson.JsonArray
import com.google.gson.annotations.SerializedName


data class PlaceDetail (
    @SerializedName("html_attributions")
    val htmlAttributions: JsonArray,

    val result: Result,
    val status: String
)


data class Result (
    @SerializedName("address_components")
    val addressComponents: List<AddressComponent>,

    @SerializedName("adr_address")
    val adrAddress: String,

    @SerializedName("business_status")
    val businessStatus: String,

    @SerializedName("formatted_address")
    val formattedAddress: String,

    @SerializedName("formatted_phone_number")
    val formattedPhoneNumber: String,

    val geometry: Geometry,
    val icon: String,

    @SerializedName("icon_background_color")
    val iconBackgroundColor: String,

    @SerializedName("icon_mask_base_uri")
    val iconMaskBaseuri: String,

    @SerializedName("international_phone_number")
    val internationalPhoneNumber: String,

    val name: String,

    @SerializedName("opening_hours")
    val openingHours: OpeningHours,

    val photos: List<Photo>,

    @SerializedName("place_id")
    val placeid: String,

    @SerializedName("plus_code")
    val plusCode: PlusCode,

    val rating: Long,
    val reference: String,
    val reviews: List<Review>,
    val types: List<String>,
    val url: String,

    @SerializedName("user_ratings_total")
    val userRatingsTotal: Long,

    @SerializedName("utc_offset")
    val utcOffset: Long,

    val vicinity: String,
    val website: String
)


data class AddressComponent (
    @SerializedName("long_name")
    val longName: String,

    @SerializedName("short_name")
    val shortName: String,

    val types: List<String>
)


data class Geometry (
    val location: Location,
    val viewport: Viewport
)


data class Location (
    val lat: Double,
    val lng: Double
)


data class Viewport (
    val northeast: Location,
    val southwest: Location
)


data class OpeningHours (
    @SerializedName("open_now")
    val openNow: Boolean,

    val periods: List<Period>,

    @SerializedName("weekday_text")
    val weekdayText: List<String>
)


data class Period (
    val close: Close,
    val open: Close
)


data class Close (
    val day: Long,
    val time: String
)


data class Photo (
    val height: Long,

    @SerializedName("html_attributions")
    val htmlAttributions: List<String>,

    @SerializedName("photo_reference")
    val photoReference: String,

    val width: Long
)


data class PlusCode (
    @SerializedName("compound_code")
    val compoundCode: String,

    @SerializedName("global_code")
    val globalCode: String
)


data class Review (
    @SerializedName("author_name")
    val authorName: String,

    @SerializedName("author_url")
    val authorurl: String,

    val language: String,

    @SerializedName("profile_photo_url")
    val profilePhotourl: String,

    val rating: Long,

    @SerializedName("relative_time_description")
    val relativeTimeDescription: String,

    val text: String,
    val time: Long
)
