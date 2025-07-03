package de.dbaelz.siloshare.feature.settings

import com.russhwolf.settings.MapSettings
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class MultiplatformSettingsRepositoryTest {
    private lateinit var settings: MapSettings
    private lateinit var repository: SettingsRepository

    @BeforeTest
    fun setUp() {
        settings = MapSettings()
        repository = MultiplatformSettingsRepository(settings)
    }

    @Test
    fun testSetAndGetHostAddress() {
        val host = "http://example.com"

        repository.setHostAddress(host)

        assertEquals(host, repository.getHostAddress())
    }

    @Test
    fun testSetAndGetUsername() {
        val username = "user123"

        repository.setUsername(username)

        assertEquals(username, repository.getUsername())
    }

    @Test
    fun testSetAndGetPassword() {
        val password = "password1234"

        repository.setPassword(password)

        assertEquals(password, repository.getPassword())
    }

    @Test
    fun testDefaultValues() {
        // Assuming DEFAULT_HOST, DEFAULT_USERNAME, DEFAULT_PASSWORD are defined in the class
        assertEquals(SettingsRepository.DEFAULT_HOST, repository.getHostAddress())
        assertEquals(SettingsRepository.DEFAULT_USERNAME, repository.getUsername())
        assertEquals(SettingsRepository.DEFAULT_PASSWORD, repository.getPassword())
    }
}

