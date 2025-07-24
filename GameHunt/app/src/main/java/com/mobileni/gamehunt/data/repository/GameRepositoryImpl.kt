package com.mobileni.gamehunt.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mobileni.gamehunt.data.mapper.toDomain
import com.mobileni.gamehunt.data.network.RawgApiService
import com.mobileni.gamehunt.domain.model.Game
import com.mobileni.gamehunt.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Implementation of the [GameRepository] interface that handles data retrieval from the RAWG API.
 *
 * This class is responsible for fetching popular games and executing game searches
 * using the appropriate parameters. It maps the API responses into domain models.
 */
class GameRepositoryImpl @Inject constructor(
    private val api: RawgApiService,
    private val apiKey: String
) : GameRepository {

    /**
     * Fetches a list of popular games using the "metacritic=90,100" filter.
     * Returns a Flow emitting a Result with the mapped list of Game domain models.
     */
    override fun getPopularGames(): Flow<Result<List<Game>>> = flow {
        val response = api.getGames(apiKey, metacritic = "90,100", page = 1, pageSize = 10)
        emit(Result.success(response.results.map { it.toDomain() }))
    }.catch { emit(Result.failure(it)) }

    /**
     * Searches for games based on an optional genre slug and/or search term.
     * Returns a Flow of PagingData to support infinite scroll behavior.
     */
    override fun searchGames(genreSlug: String?, search: String?): Flow<PagingData<Game>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = {
                GamesPagingSource(api, apiKey, genreSlug, search)
            }
        ).flow
    }
}