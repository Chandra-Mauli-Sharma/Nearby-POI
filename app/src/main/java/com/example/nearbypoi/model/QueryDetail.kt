package com.example.nearbypoi.model

import com.google.gson.annotations.SerializedName


data class QueryDetail (
    val predictions: List<Prediction>,
    val status: String
)


data class Prediction (
    val description: String,

    @SerializedName("matched_substrings")
    val matchedSubstrings: List<MatchedSubstring>,

    @SerializedName("structured_formatting")
    val structuredFormatting: StructuredFormatting,

    val terms: List<Term>
)


data class MatchedSubstring (
    val length: Long,
    val offset: Long
)


data class StructuredFormatting (
    @SerializedName("main_text")
    val mainText: String,

    @SerializedName("main_text_matched_substrings")
    val mainTextMatchedSubstrings: List<MatchedSubstring>,

    @SerializedName("secondary_text")
    val secondaryText: String,

    @SerializedName("secondary_text_matched_substrings")
    val secondaryTextMatchedSubstrings: List<MatchedSubstring>
)


data class Term (
    val offset: Long,
    val value: String
)

