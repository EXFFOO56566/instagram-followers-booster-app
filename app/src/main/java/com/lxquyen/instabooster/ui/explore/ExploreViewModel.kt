package com.lxquyen.instabooster.ui.explore

import androidx.lifecycle.viewModelScope
import com.lxquyen.instabooster.data.model.User
import com.lxquyen.instabooster.data.repository.UserRepository
import com.lxquyen.instabooster.ui.base.BaseViewModel
import com.lxquyen.instabooster.utils.PrefKeys
import com.ohayo.core.extension.withLatestFrom
import com.ohayo.core.helper.preferences.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Furuichi on 07/07/2022
 */
@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val userRepos: UserRepository,
    private val dataStore: DataStoreManager
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
        handleUpdateActivity()

        handleGetCurrentUser()

        //#UiAction.Refresh
        handleRefreshAction()

        //#UiAction.Swiped
        handleSwipedAction()

        //#UiAction.Follow
        handleFollowAction()

        //#UiAction.CheckFollow
        handleCheckFollowAction()

        viewModelScope.launch {
            _actionEvent.emit(UiAction.Refresh)
        }
    }

    private fun handleUpdateActivity() {
        userRepos.updateActivity()
            .catch { }
            .launchIn(viewModelScope)
    }

    private fun handleGetCurrentUser() {
        userRepos.getCurrentUser()
            .map { user ->
                return@map _uiStateEvent.value.copy(stars = user.stars ?: 0)
            }
            .onEach(_uiStateEvent::emit)
            .launchIn(viewModelScope)
    }

    private fun handleRefreshAction() {
        _actionEvent.filterIsInstance<UiAction.Refresh>()
            .flatMapLatest(this::getFeedsTask)
            .map { users ->
                return@map _uiStateEvent.value.copy(
                    users = users,
                    topUser = users.firstOrNull()
                )
            }
            .onEach(_uiStateEvent::emit)
            .launchIn(viewModelScope)
    }

    private fun handleSwipedAction() {
        _actionEvent.filterIsInstance<UiAction.Swiped>()
            .withLatestFrom(_uiStateEvent) { action, state ->
                return@withLatestFrom state.copy(
                    topUser = state.users.getOrNull(action.position + 1)
                )
            }
            .onEach(_uiStateEvent::emit)
            .launchIn(viewModelScope)
    }

    private fun handleFollowAction() {
        _actionEvent.filterIsInstance<UiAction.Follow>()
            .withLatestFrom(_uiStateEvent) { _, state ->
                return@withLatestFrom state.topUser
            }
            .filterNotNull()
            .onEach {
                dataStore.set(PrefKeys.FEED_ID_TMP, it.id)
            }
            .map(UiDestination::Web)
            .onEach(_uiDestinationEvent::emit)
            .launchIn(viewModelScope)
    }

    private fun handleCheckFollowAction() {
        _actionEvent.filterIsInstance<UiAction.CheckFollow>()
            .debounce(300)
            .map {
                return@map dataStore.get(PrefKeys.FEED_ID_TMP, "") ?: ""
            }
            .filter { it.isNotEmpty() }
            .flatMapLatest(this::followTask)
            .onEach(_uiDestinationEvent::emit)
            .launchIn(viewModelScope)
    }

    private fun getFeedsTask(trigger: UiAction.Refresh): Flow<List<User>> {
        return userRepos.getFeeds(0)
            .transformLoading()
    }

    private fun followTask(feedId: String): Flow<UiDestination> {
        return userRepos.follow(feedId)
            .transformLoadingWithoutError()
            .map {
                return@map UiDestination.Reward
            }
    }

    sealed class UiAction {
        object Refresh : UiAction()
        object Follow : UiAction()

        object CheckFollow : UiAction()

        data class Swiped(val position: Int) : UiAction()
    }

    data class UiState(
        val users: List<User> = emptyList(),
        val topUser: User? = null,
        val stars: Int = 0
    )

    sealed class UiDestination {
        data class Web(val user: User) : UiDestination()

        object Reward : UiDestination()
    }
}