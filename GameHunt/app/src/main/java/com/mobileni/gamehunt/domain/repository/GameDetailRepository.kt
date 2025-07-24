package com.mobileni.gamehunt.domain.repository

import com.mobileni.gamehunt.domain.model.GameDetail
import com.mobileni.gamehunt.domain.model.Screenshot
import kotlinx.coroutines.flow.Flow

interface GameDetailRepository {
    fun getGameDetail(slug: String): Flow<Result<GameDetail>>
    fun getGameScreenshots(id: Int): Flow<Result<List<Screenshot>>>
}