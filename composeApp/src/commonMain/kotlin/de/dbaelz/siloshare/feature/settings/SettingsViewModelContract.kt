package de.dbaelz.siloshare.feature.settings

object SettingsViewModelContract {
    data class State(
        val host: String,
        val username: String,
        val password: String
    )

    sealed interface Event {
        data class UpdateSettings(
            val host: String,
            val username: String,
            val password: String
        ) : Event
    }
}