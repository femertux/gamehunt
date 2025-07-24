package com.mobileni.gamehunt.data.mapper

import com.mobileni.gamehunt.data.model.GameDetailResponse
import com.mobileni.gamehunt.domain.model.GameDetail

fun GameDetailResponse.toDomain(): GameDetail {
    return GameDetail(
        id = id,
        slug = slug,
        name = title,
        description = description.orEmpty(),
        backgroundImage = backgroundImageUrl.orEmpty(),
        rating = averageRating,
        dominantColor = dominantColor.orEmpty(),
        genres = genres?.map { it.toDomain() } ?: emptyList(),
        website = website.orEmpty()
    )
}
