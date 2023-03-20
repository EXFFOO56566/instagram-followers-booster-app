package com.lxquyen.instabooster.ui.boot

import androidx.lifecycle.viewModelScope
import com.lxquyen.instabooster.data.model.Boost
import com.lxquyen.instabooster.data.repository.UserRepository
import com.lxquyen.instabooster.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

/**
 * Created by Furuichi on 7/7/2022
 */
@HiltViewModel
class BoostViewModel @Inject constructor(
    private val userRepos: UserRepository
) : BaseViewModel() {

    sealed class UiAction {
        data class UseNow(val boostId: String) : UiAction()
    }

    data class UiState(
        val packs: List<Boost> = emptyList(),
        val stars: Int = 0
    )

    sealed class UiDestination {
        object Boost : UiDestination()
    }

    /**
     * Stream of immutable states representative of the UI.
     */
    val uiState: StateFlow<UiState>
        get() = _uiStateEvent

    val uiDestination: SharedFlow<UiDestination>
        get() = _uiDestinationEvent

    /**
     * Processor of side effects from the UI which in turn feedback into [indicatorEvent]
     */
    fun accept(action: UiAction) {
        _actionEvent.tryEmit(action)
    }

    private val _actionEvent = MutableSharedFlow<UiAction>(extraBufferCapacity = 1)
    private val _uiDestinationEvent = MutableSharedFlow<UiDestination>(extraBufferCapacity = 1)
    private val _uiStateEvent = MutableStateFlow(value = UiState())

    init {
        fetchData()

        //#UiAction.UseNow
        handleUseNowAction()
    }

    private fun fetchData() {
        fun getBoostTask() = userRepos.getBoost()
            .map { boosts ->
                boosts.forEach {
                    it.isUseNow = this::checkUseNow
                }
                return@map boosts
            }

        combine(
            getBoostTask(),
            userRepos.getCurrentUser(),
            ::Pair
        )
            .map {
                return@map UiState(packs = it.first, stars = it.second.stars ?: 0)
            }
            .onEach(_uiStateEvent::emit)
            .launchIn(viewModelScope)

    }

    private fun handleUseNowAction() {
        _actionEvent.filterIsInstance<UiAction.UseNow>()
            .flatMapLatest(this::boostTask)
            .onEach(_uiDestinationEvent::emit)
            .launchIn(viewModelScope)
    }

    private fun checkUseNow(star: Int): Boolean {
        val currentStars = _uiStateEvent.value.stars
        return currentStars >= star
    }

    private fun boostTask(action: UiAction.UseNow): Flow<UiDestination> {
        return userRepos.boost(action.boostId)
            .transformLoading()
            .map { return@map UiDestination.Boost }
    }
}