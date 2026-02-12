package deepankumarpn.github.io.eggchef.presentation.ui.screens.identifier

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import deepankumarpn.github.io.eggchef.base.BaseViewModel
import deepankumarpn.github.io.eggchef.domain.usecase.identifier.GetIdentifiersUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IdentifierViewModel @Inject constructor(
    private val getIdentifiersUseCase: GetIdentifiersUseCase
) : BaseViewModel<IdentifierContract.Event, IdentifierContract.State, IdentifierContract.Effect>() {

    override fun createInitialState(): IdentifierContract.State = IdentifierContract.State()

    init {
        setEvent(IdentifierContract.Event.LoadIdentifiers)
    }

    override fun handleEvent(event: IdentifierContract.Event) {
        when (event) {
            is IdentifierContract.Event.LoadIdentifiers -> loadIdentifiers()
            is IdentifierContract.Event.RefreshIdentifiers -> loadIdentifiers()
        }
    }

    private fun loadIdentifiers() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            try {
                val identifiers = getIdentifiersUseCase()
                setState { copy(identifiers = identifiers, isLoading = false) }
            } catch (e: Exception) {
                setState { copy(error = e.message, isLoading = false) }
                setEffect { IdentifierContract.Effect.ShowError(e.message ?: "Failed to load identifiers") }
            }
        }
    }
}
