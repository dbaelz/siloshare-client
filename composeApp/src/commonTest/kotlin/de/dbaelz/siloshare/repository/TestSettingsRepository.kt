package de.dbaelz.siloshare.repository

import de.dbaelz.siloshare.repository.SettingsRepository.Companion.DEFAULT_HOST
import de.dbaelz.siloshare.repository.SettingsRepository.Companion.DEFAULT_PASSWORD
import de.dbaelz.siloshare.repository.SettingsRepository.Companion.DEFAULT_USERNAME
import io.ktor.http.DEFAULT_PORT

class TestSettingsRepository : SettingsRepository {
    private var testHost = DEFAULT_HOST
    private var testPort = DEFAULT_PORT
    private var testUsername = DEFAULT_USERNAME
    private var testPassword = DEFAULT_PASSWORD

    override fun getHostAddress() = testHost
    override fun getPort(): Int = testPort
    override fun getUsername() = testUsername
    override fun getPassword() = testPassword

    override fun setHostAddress(host: String) {
        this.testHost = host
    }

    override fun setPort(port: Int) {
        this.testPort = port
    }

    override fun setUsername(username: String) {
        this.testUsername = username
    }

    override fun setPassword(password: String) {
        this.testPassword = password
    }
}