package de.dbaelz.siloshare

import androidx.navigation.NavHostController
import com.russhwolf.settings.Settings
import de.dbaelz.siloshare.repository.MultiplatformSettingsRepository
import de.dbaelz.siloshare.repository.SettingsRepository
import de.dbaelz.siloshare.feature.settings.SettingsViewModel
import de.dbaelz.siloshare.network.createHttpClient
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun appModule(navHostController: NavHostController) = module {
    single<SettingsRepository> { MultiplatformSettingsRepository(Settings()) }
    single { createHttpClient(settingsRepository = get()) }

    single<ActionDispatcher> { DefaultActionDispatcher(navHostController) }

    viewModelOf(::SettingsViewModel)
}