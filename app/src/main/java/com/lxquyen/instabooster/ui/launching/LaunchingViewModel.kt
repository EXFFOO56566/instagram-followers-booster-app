package com.lxquyen.instabooster.ui.launching

import androidx.lifecycle.viewModelScope
import com.lxquyen.instabooster.data.model.User
import com.lxquyen.instabooster.data.repository.UserRepository
import com.lxquyen.instabooster.ui.base.BaseViewModel
import com.ohayo.core.extension.withLatestFrom
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

/**
 * Created by Furuichi on 07/07/2022
 */
@HiltViewModel
class LaunchingViewModel @Inject constructor(
    private val userRepos: UserRepository
) : BaseViewModel() {
    /**
     * Stream of immutable states representative of the UI.
     */
    val uiState: StateFlow<UiState>
        get() = _uiStateEvent

    val uiDestination: SharedFlow<UiDestination>
        get() = _uiDestinationEvent

    /**
     * Processor of side effects from the UI which in turn feedback into [accept]
     */
    fun accept(action: UiAction) {
        _actionEvent.tryEmit(action)
    }

    private val _actionEvent = MutableSharedFlow<UiAction>(extraBufferCapacity = 1)
    private val _uiDestinationEvent = MutableSharedFlow<UiDestination>(extraBufferCapacity = 1)
    private val _uiStateEvent = MutableStateFlow(value = UiState())

    init {
        handleInputChanged()

        handleSubmit()
    }

    private fun handleInputChanged() {
        _actionEvent.filterIsInstance<UiAction.InputChanged>()
            .map {
                val username = it.username
                val enable = username.isNotEmpty()
                return@map UiState(enable, username)
            }
            .onEach(_uiStateEvent::emit)
            .launchIn(viewModelScope)
    }

    private fun handleSubmit() {
        _actionEvent.filterIsInstance<UiAction.Submit>()
            .withLatestFrom(_uiStateEvent) { _, state -> return@withLatestFrom state.username }
            .flatMapLatest(this::handleLogin)
            .onEach(_uiDestinationEvent::emit)
            .launchIn(viewModelScope)
    }

    private fun handleLogin(username: String): Flow<UiDestination> {
        return userRepos.login(username)
            .transformLoading()
            .map(UiDestination::Main)
    }

    sealed class UiAction {
        object Submit : UiAction()

        data class InputChanged(val username: String) : UiAction()
    }

    data class UiState(
        val enable: Boolean = false,
        val username: String = ""
    )

    sealed class UiDestination {
        data class Main(val user: User) : UiDestination()
    }
}