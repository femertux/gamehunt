package com.mobileni.gamehunt.data.model

import com.google.gson.annotations.SerializedName

data class GameListResponse(
    @SerializedName("count") val count: Int,
    @SerializedName("next") val next: String?,
    @SerializedName("previous") val previous: String?,
    @SerializedName("results") val results: List<GameResponse>
)

data class GameResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("slug") val slug: String,
    @SerializedName("background_image") val image: String?,
    @SerializedName("rating") val rating: Float,
    @SerializedName("released") val releaseDate: String?,
    @SerializedName("genres") val genres: List<GenreTagResponse>
)

data class GenreTagResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)