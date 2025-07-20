package de.dbaelz.siloshare

import de.dbaelz.siloshare.navigation.Action
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class TestActionDispatcher() : ActionDispatcher {
    private val _events = MutableSharedFlow<Action>(replay = 0, extraBufferCapacity = 1)
    override val events: SharedFlow<Action>
        get() = _events

    override fun dispatch(action: Action) {
        _events.tryEmit(action)
    }
}