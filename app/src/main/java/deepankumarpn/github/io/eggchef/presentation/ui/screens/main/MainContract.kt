package deepankumarpn.github.io.eggchef.presentation.ui.screens.main

import deepankumarpn.github.io.eggchef.base.UiEffect
import deepankumarpn.github.io.eggchef.base.UiEvent
import deepankumarpn.github.io.eggchef.base.UiState

class MainContract {

    sealed class Event : UiEvent

    data class State(
        val isLoading: Boolean = false
    ) : UiState

    sealed class Effect : UiEffect
}
