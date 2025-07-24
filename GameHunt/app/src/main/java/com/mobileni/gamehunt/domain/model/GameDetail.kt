package com.mobileni.gamehunt.domain.model

data class GameDetail(
    val id: Int,
    val slug: String,
    val name: String,
    val description: String,
    val backgroundImage: String,
    val rating: Double,
    val dominantColor: String,
    val genres: List<Genre>,
    val website: String
)