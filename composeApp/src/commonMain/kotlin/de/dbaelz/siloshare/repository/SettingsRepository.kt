package de.dbaelz.siloshare.repository

import com.russhwolf.settings.Settings
import de.dbaelz.siloshare.repository.SettingsRepository.Companion.DEFAULT_HOST
import de.dbaelz.siloshare.repository.SettingsRepository.Companion.DEFAULT_PASSWORD
import de.dbaelz.siloshare.repository.SettingsRepository.Companion.DEFAULT_PORT
import de.dbaelz.siloshare.repository.SettingsRepository.Companion.DEFAULT_USERNAME

interface SettingsRepository {
    fun setHostAddress(host: String)
    fun getHostAddress(): String
    fun setPort(port: Int)
    fun getPort(): Int
    fun setUsername(username: String)
    fun getUsername(): String
    fun setPassword(password: String)
    fun getPassword(): String

    companion object {
        const val DEFAULT_HOST = "http://localhost"
        const val DEFAULT_PORT = 8080
        const val DEFAULT_USERNAME = "user"
        const val DEFAULT_PASSWORD = "password"
    }
}

class MultiplatformSettingsRepository(val settings: Settings) : SettingsRepository {
    override fun setHostAddress(host: String) =
        settings.putString(KEY_HOST, host)

    override fun getHostAddress(): String =
        settings.getStringOrNull(KEY_HOST) ?: DEFAULT_HOST

    override fun setPort(port: Int) =
        settings.putInt(KEY_PORT, port)

    override fun getPort(): Int =
        settings.getIntOrNull(KEY_PORT) ?: DEFAULT_PORT

    override fun setUsername(username: String) =
        settings.putString(KEY_USERNAME, username)

    override fun getUsername(): String =
        settings.getStringOrNull(KEY_USERNAME) ?: DEFAULT_USERNAME

    override fun setPassword(password: String) =
        settings.putString(KEY_PASSWORD, password)

    override fun getPassword(): String =
        settings.getStringOrNull(KEY_PASSWORD) ?: DEFAULT_PASSWORD

    companion object {
        private const val KEY_HOST = "HOST"
        private const val KEY_PORT = "PORT"
        private const val KEY_USERNAME = "USERNAME"
        private const val KEY_PASSWORD = "PASSWORD"
    }
}