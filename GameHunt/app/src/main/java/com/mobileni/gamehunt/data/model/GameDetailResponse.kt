package com.mobileni.gamehunt.data.model

import com.google.gson.annotations.SerializedName

data class GameDetailResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("slug")
    val slug: String,
    @SerializedName("name")
    val title: String,
    @SerializedName("description_raw")
    val description: String?,
    @SerializedName("background_image")
    val backgroundImageUrl: String?,
    @SerializedName("rating")
    val averageRating: Double,
    @SerializedName("dominant_color")
    val dominantColor: String?,
    @SerializedName("genres")
    val genres: List<GenreModel>?,
    @SerializedName("website")
    val website: String?
)