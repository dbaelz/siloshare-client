package de.dbaelz.siloshare.feature.settings

import com.russhwolf.settings.Settings

class SettingsRepository(val settings: Settings) {
    fun setHostAddress(host: String) {
        settings.putString(KEY_HOST, host)
    }

    fun getHostAddress(): String =
        settings.getStringOrNull(KEY_HOST) ?: DEFAULT_HOST

    fun setUsername(username: String) {
        settings.putString(KEY_USERNAME, username)
    }

    fun getUsername(): String? =
        settings.getStringOrNull(KEY_USERNAME) ?: DEFAULT_USERNAME

    fun setPassword(password: String) {
        settings.putString(KEY_PASSWORD, password)
    }

    fun getPassword(): String? =
        settings.getStringOrNull(KEY_PASSWORD) ?: DEFAULT_PASSWORD

    companion object {
        private const val KEY_HOST = "HOST"
        private const val KEY_USERNAME = "USERNAME"
        private const val KEY_PASSWORD = "PASSWORD"

        const val DEFAULT_HOST = "http://localhost:8080"
        const val DEFAULT_USERNAME = "user"
        const val DEFAULT_PASSWORD = "password"
    }
}