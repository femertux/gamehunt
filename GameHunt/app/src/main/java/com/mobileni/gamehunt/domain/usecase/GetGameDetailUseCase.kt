package com.mobileni.gamehunt.domain.usecase

import com.mobileni.gamehunt.domain.model.GameDetail
import com.mobileni.gamehunt.domain.repository.GameDetailRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case that provides game detail data by delegating to the GameDetailRepository.
 *
 * This class allows separation of concerns by isolating the business logic
 * of fetching game detail data from the ViewModel or UI layer.
 */
class GetGameDetailUseCase @Inject constructor(
    private val repository: GameDetailRepository
) {
    /**
     * Executes the use case by returning a Flow<Result<GameDetail>>.
     * The Flow emits the loading result from the repository.
     */
    operator fun invoke(slug: String): Flow<Result<GameDetail>> {
        return repository.getGameDetail(slug)
    }
}