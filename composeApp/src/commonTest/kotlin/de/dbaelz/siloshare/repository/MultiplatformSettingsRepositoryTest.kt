package de.dbaelz.siloshare.repository

import com.russhwolf.settings.MapSettings
import kotlin.test.Test
import kotlin.test.assertEquals

class MultiplatformSettingsRepositoryTest {
    @Test
    fun defaultValues() {
        val repo = MultiplatformSettingsRepository(MapSettings())
        assertEquals(SettingsRepository.DEFAULT_HOST, repo.getHostAddress())
        assertEquals(SettingsRepository.DEFAULT_PORT, repo.getPort())
        assertEquals(SettingsRepository.DEFAULT_USERNAME, repo.getUsername())
        assertEquals(SettingsRepository.DEFAULT_PASSWORD, repo.getPassword())
    }

    @Test
    fun setAndGetHostAddress() {
        val repo = MultiplatformSettingsRepository(MapSettings())
        repo.setHostAddress("192.168.1.1")
        assertEquals("192.168.1.1", repo.getHostAddress())
    }

    @Test
    fun setAndGetPort() {
        val repo = MultiplatformSettingsRepository(MapSettings())
        repo.setPort(1234)
        assertEquals(1234, repo.getPort())
    }

    @Test
    fun setAndGetUsername() {
        val repo = MultiplatformSettingsRepository(MapSettings())
        repo.setUsername("admin")
        assertEquals("admin", repo.getUsername())
    }

    @Test
    fun setAndGetPassword() {
        val repo = MultiplatformSettingsRepository(MapSettings())
        repo.setPassword("secret")
        assertEquals("secret", repo.getPassword())
    }
}
