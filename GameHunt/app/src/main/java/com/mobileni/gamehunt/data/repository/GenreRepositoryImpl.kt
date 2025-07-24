package com.mobileni.gamehunt.data.repository

import com.mobileni.gamehunt.data.mapper.toDomain
import com.mobileni.gamehunt.data.network.RawgApiService
import com.mobileni.gamehunt.domain.model.Genre
import com.mobileni.gamehunt.domain.repository.GenreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Implementation of [GenreRepository] that communicates with the RAWG API
 * to fetch a list of available game genres.
 */
class GenreRepositoryImpl @Inject constructor(
    private val api: RawgApiService,
    private val apiKey: String
) : GenreRepository {
    override fun getGenres(): Flow<Result<List<Genre>>> = flow {
        val response = api.getGenres(apiKey)
        emit(Result.success(response.results.map { it.toDomain() }))
    }.catch {
        emit(Result.failure(it))
    }
}