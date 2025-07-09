package de.dbaelz.siloshare.feature.settings

object SettingsViewModelContract {
    data class State(
        val host: String,
        val port: Int,
        val username: String,
        val password: String,
        val isSaved: Boolean = false
    )

    sealed interface Event {
        data class OnValuesChanged(
            val host: String,
            val port: Int,
            val username: String,
            val password: String
        ) : Event
    }
}