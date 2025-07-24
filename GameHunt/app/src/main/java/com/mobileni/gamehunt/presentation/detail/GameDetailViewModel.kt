package com.mobileni.gamehunt.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobileni.gamehunt.domain.di.IoDispatcher
import com.mobileni.gamehunt.domain.model.GameDetail
import com.mobileni.gamehunt.domain.model.Screenshot
import com.mobileni.gamehunt.domain.usecase.GetGameDetailUseCase
import com.mobileni.gamehunt.domain.usecase.GetGameScreenshotsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameDetailViewModel @Inject constructor(
    private val getGameDetailUseCase: GetGameDetailUseCase,
    private val getGameScreenshotsUseCase: GetGameScreenshotsUseCase,
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher
) : ViewModel() {

    // Holds UI state for game details (loading, error, game info)
    private val _gameDetailState = MutableStateFlow(GameDetailUiState())
    val gameDetailState: StateFlow<GameDetailUiState> = _gameDetailState

    // Holds UI state for game screenshots
    private val _screenshotState = MutableStateFlow(ScreenshotUiState())
    val screenshotState: StateFlow<ScreenshotUiState> = _screenshotState

    // SharedFlow to emit one-time effects like share intents
    private val _effect = MutableSharedFlow<GameDetailEffect>()
    val effect = _effect.asSharedFlow()

    // Handles events from the UI layer
    fun onEvent(event: GameDetailEvent) {
        when (event) {
            is GameDetailEvent.Load -> loadGameDetail(event.slug)
            is GameDetailEvent.Retry -> {
                _gameDetailState.value.detail?.slug?.let {
                    loadGameDetail(it)
                }
            }
            is GameDetailEvent.ShareGame -> {
                // Emits a ShareIntent effect to be collected in the UI
                viewModelScope.launch {
                    _effect.emit(
                        GameDetailEffect.ShareIntent(
                            title = "Check out this game!",
                            text = "Take a look at ${event.name} on GameHunt!\n${event.website}"
                        )
                    )
                }
            }
        }
    }

    // Loads the game detail and triggers screenshot loading on success
    private fun loadGameDetail(slug: String) {
        getGameDetailUseCase(slug)
            .onStart {
                _gameDetailState.update {
                    it.copy(
                        isLoading = true,
                        error = "",
                        detail = null
                    )
                }
            }
            .map { result ->
                result.fold(
                    onSuccess = { detail ->
                        _gameDetailState.update {
                            it.copy(
                                detail = detail,
                                isLoading = false,
                                error = ""
                            )
                        }
                        loadScreenshots(detail.id)
                    },
                    onFailure = { exception ->
                        _gameDetailState.update {
                            it.copy(
                                error = exception.message.orEmpty(),
                                isLoading = false,
                                detail = null
                            )
                        }
                    }
                )
            }
            .flowOn(coroutineDispatcher)
            .launchIn(viewModelScope)
    }

    // Loads screenshots for a given game ID
    private fun loadScreenshots(id: Int) {
        getGameScreenshotsUseCase(id)
            .onStart {
                _screenshotState.update {
                    it.copy(
                        isLoading = true,
                        screenshots = emptyList(),
                        error = ""
                    )
                }
            }
            .map { result ->
                result.fold(
                    onSuccess = { screenshots ->
                        _screenshotState.update {
                            it.copy(
                                screenshots = screenshots,
                                isLoading = false,
                                error = ""
                            )
                        }
                    },
                    onFailure = { e ->
                        _screenshotState.update {
                            it.copy(
                                error = e.message.orEmpty(),
                                isLoading = false,
                                screenshots = emptyList()
                            )
                        }
                    }
                )
            }
            .flowOn(coroutineDispatcher)
            .launchIn(viewModelScope)
    }
}

// Represents the state of the Game Detail UI
data class GameDetailUiState(
    val isLoading: Boolean = false,
    val detail: GameDetail? = null,
    val error: String = ""
)

// Represents the state of the Screenshot list
data class ScreenshotUiState(
    val isLoading: Boolean = false,
    val screenshots: List<Screenshot> = emptyList(),
    val error: String = ""
)

// Defines the events that the ViewModel can handle
sealed class GameDetailEvent {
    data class Load(val slug: String) : GameDetailEvent()
    object Retry : GameDetailEvent()
    data class ShareGame(val name: String, val website: String) : GameDetailEvent()
}

// Defines one-time UI effects such as navigation or intent
sealed class GameDetailEffect {
    data class ShareIntent(val title: String, val text: String) : GameDetailEffect()
}