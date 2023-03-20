package com.lxquyen.instabooster.ui.profile

import androidx.lifecycle.viewModelScope
import com.lxquyen.instabooster.data.model.User
import com.lxquyen.instabooster.data.repository.UserRepository
import com.lxquyen.instabooster.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

/**
 * Created by Furuichi on 07/07/2022
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepos: UserRepository
) : BaseViewModel() {
    /**
     * Stream of immutable states representative of the UI.
     */
    val uiState: StateFlow<UiState>
        get() = _uiStateEvent

    /**
     * Processor of side effects from the UI which in turn feedback into [accept]
     */
    fun accept(action: UiAction) {
        _actionEvent.tryEmit(action)
    }

    private val _actionEvent = MutableSharedFlow<UiAction>(extraBufferCapacity = 1)
    private val _uiStateEvent = MutableStateFlow(value = UiState())

    init {
        handleGetCurrentUser()
        handleWatchAction()
        handleFollowOurInsta()
    }

    private fun handleFollowOurInsta() {
        _actionEvent.filterIsInstance<UiAction.FollowOurInsta>()
            .flatMapLatest {
                return@flatMapLatest userRepos.followOurInsta()
                    .transformLoading()
            }
            .launchIn(viewModelScope)
    }

    private fun handleWatchAction() {
        _actionEvent.filterIsInstance<UiAction.Watch>()
            .flatMapLatest {
                return@flatMapLatest userRepos.watch()
                    .transformLoading()
            }
            .launchIn(viewModelScope)
    }

    private fun handleGetCurrentUser() {
        userRepos.getCurrentUser()
            .map(::UiState)
            .onEach(_uiStateEvent::emit)
            .launchIn(viewModelScope)
    }

    sealed class UiAction {
        object Watch : UiAction()
        object FollowOurInsta : UiAction()
    }

    data class UiState(
        val user: User? = null
    )
}