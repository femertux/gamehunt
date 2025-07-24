package com.mobileni.gamehunt.data.model

import com.google.gson.annotations.SerializedName

data class ScreenshotResponse(
    @SerializedName("results") val results: List<ScreenshotItemResponse>
)

data class ScreenshotItemResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("image") val image: String
)