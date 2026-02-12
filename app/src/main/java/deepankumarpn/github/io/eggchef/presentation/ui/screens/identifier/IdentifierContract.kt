package deepankumarpn.github.io.eggchef.presentation.ui.screens.identifier

import deepankumarpn.github.io.eggchef.base.UiEffect
import deepankumarpn.github.io.eggchef.base.UiEvent
import deepankumarpn.github.io.eggchef.base.UiState
import deepankumarpn.github.io.eggchef.domain.model.AppIdentifiers

class IdentifierContract {

    sealed class Event : UiEvent {
        data object LoadIdentifiers : Event()
        data object RefreshIdentifiers : Event()
    }

    data class State(
        val identifiers: AppIdentifiers? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed class Effect : UiEffect {
        data class ShowError(val message: String) : Effect()
    }
}
