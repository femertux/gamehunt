package com.mobileni.gamehunt.domain.repository

import com.mobileni.gamehunt.domain.model.Genre
import kotlinx.coroutines.flow.Flow

interface GenreRepository {
    fun getGenres(): Flow<Result<List<Genre>>>
}