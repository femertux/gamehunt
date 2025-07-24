package com.mobileni.gamehunt.domain.usecase

import com.mobileni.gamehunt.domain.model.Game
import com.mobileni.gamehunt.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case responsible for fetching the list of popular games.
 *
 * Delegates the logic to the repository and exposes the data as a Flow.
 * Promotes separation of concerns between the UI and data layers.
 */
class GetPopularGamesUseCase @Inject constructor(
    private val repository: GameRepository
) {
    /**
     * Executes the use case and returns a Flow emitting the result.
     */
    operator fun invoke(): Flow<Result<List<Game>>> = repository.getPopularGames()
}