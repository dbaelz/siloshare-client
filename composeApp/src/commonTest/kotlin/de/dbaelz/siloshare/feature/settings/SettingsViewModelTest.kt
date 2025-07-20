package de.dbaelz.siloshare.feature.settings

import app.cash.turbine.test
import de.dbaelz.siloshare.ActionDispatcher
import de.dbaelz.siloshare.feature.settings.SettingsViewModelContract.Event
import de.dbaelz.siloshare.navigation.Action
import de.dbaelz.siloshare.repository.SettingsRepository.Companion.DEFAULT_HOST
import de.dbaelz.siloshare.repository.SettingsRepository.Companion.DEFAULT_PASSWORD
import de.dbaelz.siloshare.repository.SettingsRepository.Companion.DEFAULT_USERNAME
import de.dbaelz.siloshare.repository.TestSettingsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class TestActionDispatcher : ActionDispatcher {
    override val events: SharedFlow<Action> = MutableSharedFlow()

    override fun dispatch(action: Action) {
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    @Test
    fun `initial state is loaded from repository`() = runTest {
        val viewModel = SettingsViewModel(
            TestSettingsRepository(), actionDispatcher = TestActionDispatcher()
        )

        viewModel.state.test {
            val state = awaitItem()

            assertEquals(DEFAULT_HOST, state.host)
            assertEquals(DEFAULT_USERNAME, state.username)
            assertEquals(DEFAULT_PASSWORD, state.password)
        }
    }

    @Test
    fun `update settings event updates repository and state`() = runTest {
        val viewModel = SettingsViewModel(
            TestSettingsRepository(), actionDispatcher = TestActionDispatcher()
        )

        val newHost = "http://newhost.com"
        val newPort = 1234
        val newUsername = "newUser"
        val newPassword = "newPassword"

        viewModel.sendEvent(Event.OnValuesChanged(newHost, newPort, newUsername, newPassword))

        viewModel.state.test {
            val state = awaitItem()

            assertEquals(newHost, state.host)
            assertEquals(newUsername, state.username)
            assertEquals(newPassword, state.password)
        }
    }
}

