package com.lxquyen.instabooster.ui.main

import com.lxquyen.instabooster.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Created by Furuichi on 07/07/2022
 */
@HiltViewModel
class MainViewModel @Inject constructor(
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

    }

    sealed class UiAction {
    }

    data class UiState(
        var x: Int = 0
    )
}