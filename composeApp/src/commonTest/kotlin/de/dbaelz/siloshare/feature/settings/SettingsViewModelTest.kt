package de.dbaelz.siloshare.feature.settings

import app.cash.turbine.test
import de.dbaelz.siloshare.ActionDispatcher
import de.dbaelz.siloshare.navigation.Action
import de.dbaelz.siloshare.repository.TestSettingsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class TestActionDispatcher() : ActionDispatcher {
    private val _events = MutableSharedFlow<Action>(replay = 0, extraBufferCapacity = 1)
    override val events: SharedFlow<Action>
        get() = _events

    override fun dispatch(action: Action) {
        _events.tryEmit(action)
    }
}

class SettingsViewModelTest {
    @Test
    fun onValuesChanged_updatesState() = runTest {
        val repo = TestSettingsRepository()
        val dispatcher = TestActionDispatcher()

        val viewModel = SettingsViewModel(repo, dispatcher)

        val host = "newhost"
        val port = 1234
        val username = "newuser"
        val password = "newpass"

        val event = SettingsViewModelContract.Event.OnValuesChanged(
            host = host,
            port = port,
            username = username,
            password = password
        )

        viewModel.sendEvent(event)

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(host, state.host)
            assertEquals(port, state.port)
            assertEquals(username, state.username)
            assertEquals(password, state.password)
        }
    }

    @Test
    fun settingsSave_dispatch_updatesState() = runTest {
        val repo = TestSettingsRepository()
        val dispatcher = TestActionDispatcher()

        val viewModel = SettingsViewModel(repo, dispatcher)

        viewModel.state.test {
            val initialState = awaitItem()
            assertFalse(initialState.isSaved)

            delay(500)
            dispatcher.dispatch(Action.SettingsSave)

            val state = awaitItem()
            assertTrue(state.isSaved)
        }
    }
}
