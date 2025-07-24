package com.mobileni.gamehunt.data.mapper

import com.mobileni.gamehunt.data.model.GenreModel
import com.mobileni.gamehunt.domain.model.Genre

fun GenreModel.toDomain(): Genre {
    return Genre(
        id = this.id,
        name = this.name,
        slug = this.slug,
        imageUrl = this.imageBackground
    )
}