package com.mobileni.gamehunt.domain.repository

import androidx.paging.PagingData
import com.mobileni.gamehunt.domain.model.Game
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    fun getPopularGames(): Flow<Result<List<Game>>>
    fun searchGames(genreSlug: String?, search: String?): Flow<PagingData<Game>>
}