package com.example.nearbypoi.model

import com.example.nearbypoi.model.place_details.Geometry
import com.example.nearbypoi.model.place_details.Location
import com.example.nearbypoi.model.place_details.OpeningHours
import com.example.nearbypoi.model.place_details.Photo
import com.example.nearbypoi.model.place_details.PlusCode
import com.example.nearbypoi.model.place_details.Result
import com.example.nearbypoi.model.place_details.Viewport
import com.google.gson.JsonArray
import com.google.gson.annotations.SerializedName


data class Place(
    @SerializedName("html_attributions")
    val htmlAttributions: JsonArray,

    @SerializedName("next_page_token")
    val nextPageToken: String,

    val results: List<Result>,
    val status: String
)


data class Result (
    val geometry: Geometry,
    val icon: String,

    @SerializedName("icon_background_color")
    val iconBackgroundColor: IconBackgroundColor,

    @SerializedName("icon_mask_base_uri")
    val iconMaskBaseuri: String,

    val name: String,
    val photos: List<Photo>,

    @SerializedName("place_id")
    val placeid: String,

    val reference: String,
    val scope: Scope,
    val types: List<String>,
    val vicinity: String,

    @SerializedName("business_status")
    val businessStatus: BusinessStatus? = null,

    @SerializedName("opening_hours")
    val openingHours: OpeningHours? = null,

    @SerializedName("plus_code")
    val plusCode: PlusCode? = null,

    @SerializedName("price_level")
    val priceLevel: Long? = null,

    val rating: Double? = null,

    @SerializedName("user_ratings_total")
    val userRatingsTotal: Long? = null
)


enum class BusinessStatus(val value: String) {
    @SerializedName("OPERATIONAL") Operational("OPERATIONAL");
}


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


enum class IconBackgroundColor(val value: String) {
    @SerializedName("#FF9E67") Ff9E67("#FF9E67"),
    @SerializedName("#13B5C7") The13B5C7("#13B5C7"),
    @SerializedName("#7B9EB0") The7B9Eb0("#7B9EB0"),
    @SerializedName("#909CE1") The909Ce1("#909CE1");
}


data class OpeningHours (
    @SerializedName("open_now")
    val openNow: Boolean
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


enum class Scope(val value: String) {
    @SerializedName("GOOGLE") Google("GOOGLE");
}
