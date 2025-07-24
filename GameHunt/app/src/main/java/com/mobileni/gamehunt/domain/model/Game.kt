package com.mobileni.gamehunt.domain.model

data class Game(
    val id: Int,
    val name: String,
    val slug: String,
    val imageUrl: String,
    val rating: Float,
    val releaseDate: String?,
    val genres: List<String>
)