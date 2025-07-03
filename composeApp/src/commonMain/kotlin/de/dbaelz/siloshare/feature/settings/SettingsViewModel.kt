package de.dbaelz.siloshare.feature.settings

import androidx.lifecycle.viewModelScope
import de.dbaelz.siloshare.feature.BaseViewModel
import de.dbaelz.siloshare.feature.settings.SettingsViewModelContract.Event
import de.dbaelz.siloshare.feature.settings.SettingsViewModelContract.State
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : BaseViewModel<State, Event, Event>(
    initialState = State(
        host = settingsRepository.getHostAddress(),
        username = settingsRepository.getUsername(),
        password = settingsRepository.getPassword()
    )
) {

    init {
        viewModelScope.launch {
            event.map { reduce(state.value, it) }.collect { updateState(it) }
        }
    }

    private fun reduce(state: State, event: Event): State {
        when (event) {
            is Event.UpdateSettings -> {
                settingsRepository.setHostAddress(event.host)
                settingsRepository.setUsername(event.username)
                settingsRepository.setPassword(event.password)

                return state.copy(
                    host = event.host,
                    username = event.username,
                    password = event.password
                )
            }
        }
    }
}