package com.mobileni.gamehunt.domain.usecase

import com.mobileni.gamehunt.domain.model.Genre
import com.mobileni.gamehunt.domain.repository.GenreRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case that provides a flow of game genres from the repository.
 * Delegates the data fetching to [GenreRepository.getGenres].
 */
class GetGenresUseCase @Inject constructor(
    private val repository: GenreRepository
)  {
    operator fun invoke(): Flow<Result<List<Genre>>> {
        return repository.getGenres()
    }
}