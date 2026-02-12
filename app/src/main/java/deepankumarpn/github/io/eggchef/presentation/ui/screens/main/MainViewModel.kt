package deepankumarpn.github.io.eggchef.presentation.ui.screens.main

import dagger.hilt.android.lifecycle.HiltViewModel
import deepankumarpn.github.io.eggchef.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() :
    BaseViewModel<MainContract.Event, MainContract.State, MainContract.Effect>() {

    override fun createInitialState(): MainContract.State = MainContract.State()

    override fun handleEvent(event: MainContract.Event) {}
}
