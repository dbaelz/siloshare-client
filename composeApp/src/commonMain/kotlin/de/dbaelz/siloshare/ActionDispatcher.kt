package de.dbaelz.siloshare

import androidx.navigation.NavHostController
import de.dbaelz.siloshare.navigation.Action
import de.dbaelz.siloshare.navigation.Screen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class ActionDispatcher(
    private val navController: NavHostController
) {
    private val _events = MutableSharedFlow<Action>(replay = 0, extraBufferCapacity = 1)
    val events: SharedFlow<Action> = _events

    fun dispatch(action: Action) {
        when (action) {
            is Action.ShowSettings -> {
                navController.navigate(Screen.Settings.name)
            }

            is Action.SaveSettings -> _events.tryEmit(action)
        }
    }
}