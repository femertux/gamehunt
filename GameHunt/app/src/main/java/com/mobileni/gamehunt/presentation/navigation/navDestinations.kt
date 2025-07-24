package com.mobileni.gamehunt.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
object Home

@Serializable
data class GameDetail(val slug: String)