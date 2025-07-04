package de.dbaelz.siloshare.feature.settings

import androidx.lifecycle.viewModelScope
import de.dbaelz.siloshare.ActionDispatcher
import de.dbaelz.siloshare.feature.BaseViewModel
import de.dbaelz.siloshare.feature.settings.SettingsViewModel.InternalEvent
import de.dbaelz.siloshare.feature.settings.SettingsViewModelContract.Event
import de.dbaelz.siloshare.feature.settings.SettingsViewModelContract.State
import de.dbaelz.siloshare.navigation.Action
import de.dbaelz.siloshare.repository.SettingsRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val actionDispatcher: ActionDispatcher
) : BaseViewModel<State, Event, InternalEvent>(
    initialState = State(
        host = settingsRepository.getHostAddress(),
        port = settingsRepository.getPort(),
        username = settingsRepository.getUsername(),
        password = settingsRepository.getPassword()
    )
) {

    init {
        viewModelScope.launch {
            event.map { reduce(state.value, it) }.collect { updateState(it) }
        }

        viewModelScope.launch {
            actionDispatcher.events.collect { action ->
                if (action is Action.SaveSettings) {
                    sendEvent(
                        InternalEvent.UpdateSettings(
                            host = state.value.host,
                            port = state.value.port,
                            username = state.value.username,
                            password = state.value.password
                        )
                    )
                }
            }
        }
    }

    private fun reduce(state: State, event: Event): State {
        return when (event) {
            is InternalEvent.UpdateSettings -> {
                settingsRepository.setHostAddress(event.host)
                settingsRepository.setPort(event.port)
                settingsRepository.setUsername(event.username)
                settingsRepository.setPassword(event.password)

                state
            }

            is Event.OnValuesChanged -> {
                state.copy(
                    host = event.host,
                    port = event.port,
                    username = event.username,
                    password = event.password
                )
            }
        }
    }

    sealed interface InternalEvent : Event {
        data class UpdateSettings(
            val host: String,
            val port: Int,
            val username: String,
            val password: String
        ) : InternalEvent
    }
}