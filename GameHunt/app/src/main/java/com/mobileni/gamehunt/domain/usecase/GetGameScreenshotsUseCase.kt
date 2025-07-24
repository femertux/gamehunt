package com.mobileni.gamehunt.domain.usecase

import com.mobileni.gamehunt.domain.model.Screenshot
import com.mobileni.gamehunt.domain.repository.GameDetailRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case that retrieves a list of screenshots for a specific game.
 *
 * This use case delegates the request to the GameDetailRepository
 * and exposes a Flow<Result<List<Screenshot>>> for reactive consumption.
 * It helps separate the domain logic from the presentation layer.
 */
class GetGameScreenshotsUseCase @Inject constructor(
    private val repository: GameDetailRepository
) {
    /**
     * Executes the use case by fetching the screenshots using the given game ID.
     * Returns a Flow that emits the result of the operation.
     */
    operator fun invoke(id: Int): Flow<Result<List<Screenshot>>> {
        return repository.getGameScreenshots(id)
    }
}