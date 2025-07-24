package com.mobileni.gamehunt.data.repository

import com.mobileni.gamehunt.data.mapper.toDomain
import com.mobileni.gamehunt.data.network.RawgApiService
import com.mobileni.gamehunt.domain.model.GameDetail
import com.mobileni.gamehunt.domain.model.Screenshot
import com.mobileni.gamehunt.domain.repository.GameDetailRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Implementation of the [GameDetailRepository] interface.
 *
 * This class is responsible for retrieving detailed information and screenshots
 * of a specific game by interacting with the RAWG API service.
 *
 * It converts the network responses to domain models and wraps them in a Result
 * to handle both success and error states gracefully using Kotlin Flows.
 */
class GameDetailRepositoryImpl @Inject constructor(
    private val api: RawgApiService,
    private val apiKey: String
) : GameDetailRepository {

    /**
     * Fetches game detail data by slug from the RAWG API.
     * The result is emitted as a Flow<Result<GameDetail>>.
     */
    override fun getGameDetail(slug: String): Flow<Result<GameDetail>> = flow {
        val response = api.getGameDetail(slug = slug, apiKey = apiKey)
        emit(Result.success(response.toDomain()))
    }.catch {
        emit(Result.failure(it))
    }

    /**
     * Fetches a list of screenshots for the given game ID.
     * The result is emitted as a Flow<Result<List<Screenshot>>>.
     */
    override fun getGameScreenshots(id: Int): Flow<Result<List<Screenshot>>> = flow {
        val response = api.getGameScreenshots(id = id, apiKey = apiKey, pageSize = 10)
        emit(Result.success(response.results.map { it.toDomain() }))
    }.catch {
        emit(Result.failure(it))
    }
}