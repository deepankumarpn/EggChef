package deepankumarpn.github.io.eggchef.presentation.ui.screens.boiltimer

import deepankumarpn.github.io.eggchef.base.UiEffect
import deepankumarpn.github.io.eggchef.base.UiEvent
import deepankumarpn.github.io.eggchef.base.UiState
import deepankumarpn.github.io.eggchef.domain.model.BoilItem

class BoilTimerContract {

    sealed class Event : UiEvent {
        data object StartClicked : Event()
        data object PauseClicked : Event()
        data object ResetClicked : Event()
        data class BoilTypeSelected(val position: Int) : Event()
        data object ShowPrivacyDialog : Event()
        data object DismissPrivacyDialog : Event()
        data object ShowInstructionDialog : Event()
        data object DismissInstructionDialog : Event()
        data object NavigateToIdentifier : Event()
        data object ShowAddBoilDialog : Event()
        data object DismissAddBoilDialog : Event()
        data class AddCustomBoilType(
            val title: String,
            val minutes: Int,
            val description: String
        ) : Event()
        data class DeleteCustomBoilType(val position: Int) : Event()
    }

    data class State(
        val timerText: String = "00:00",
        val showMinus: Boolean = false,
        val startButtonText: String = "Start",
        val isStartEnabled: Boolean = true,
        val isPauseEnabled: Boolean = false,
        val isResetEnabled: Boolean = false,
        val isSpinnerEnabled: Boolean = true,
        val selectedPosition: Int = 0,
        val description: String = "",
        val boilTypes: List<BoilItem> = emptyList(),
        val infoMessage: String = "",
        val showPrivacyDialog: Boolean = false,
        val showInstructionDialog: Boolean = false,
        val showAddBoilDialog: Boolean = false,
    ) : UiState

    sealed class Effect : UiEffect {
        data object NavigateToIdentifier : Effect()
    }
}
