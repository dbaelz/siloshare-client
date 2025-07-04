package de.dbaelz.siloshare.feature.settings

import app.cash.turbine.test
import de.dbaelz.siloshare.ActionDispatcher
import de.dbaelz.siloshare.feature.settings.SettingsRepository.Companion.DEFAULT_HOST
import de.dbaelz.siloshare.feature.settings.SettingsRepository.Companion.DEFAULT_PASSWORD
import de.dbaelz.siloshare.feature.settings.SettingsRepository.Companion.DEFAULT_USERNAME
import de.dbaelz.siloshare.feature.settings.SettingsViewModelContract.Event
import de.dbaelz.siloshare.navigation.Action
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class TestSettingsRepository : SettingsRepository {
    private var testHost = DEFAULT_HOST
    private var testUsername = DEFAULT_USERNAME
    private var testPassword = DEFAULT_PASSWORD

    override fun getHostAddress() = testHost
    override fun getUsername() = testUsername
    override fun getPassword() = testPassword

    override fun setHostAddress(host: String) {
        this.testHost = host
    }

    override fun setUsername(username: String) {
        this.testUsername = username
    }

    override fun setPassword(password: String) {
        this.testPassword = password
    }
}

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
        val newUsername = "newUser"
        val newPassword = "newPassword"

        viewModel.sendEvent(Event.OnValuesChanged(newHost, newUsername, newPassword))

        viewModel.state.test {
            val state = awaitItem()

            assertEquals(newHost, state.host)
            assertEquals(newUsername, state.username)
            assertEquals(newPassword, state.password)
        }
    }
}

