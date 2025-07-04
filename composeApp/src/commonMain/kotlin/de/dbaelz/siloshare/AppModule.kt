package de.dbaelz.siloshare

import com.russhwolf.settings.Settings
import de.dbaelz.siloshare.feature.settings.MultiplatformSettingsRepository
import de.dbaelz.siloshare.feature.settings.SettingsRepository
import de.dbaelz.siloshare.feature.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single<SettingsRepository> { MultiplatformSettingsRepository(Settings()) }

    viewModelOf(::SettingsViewModel)
}