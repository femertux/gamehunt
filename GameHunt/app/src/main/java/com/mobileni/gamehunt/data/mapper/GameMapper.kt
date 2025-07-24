package com.mobileni.gamehunt.data.mapper

import com.mobileni.gamehunt.data.model.GameResponse
import com.mobileni.gamehunt.domain.model.Game

fun GameResponse.toDomain(): Game {
    return Game(
        id = id,
        name = name,
        slug = slug,
        imageUrl = image.orEmpty(),
        rating = rating,
        releaseDate = releaseDate,
        genres = genres.map { it.name }
    )
}