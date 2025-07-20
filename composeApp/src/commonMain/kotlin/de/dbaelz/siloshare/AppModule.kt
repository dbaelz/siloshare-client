package de.dbaelz.siloshare

import androidx.navigation.NavHostController
import com.russhwolf.settings.Settings
import de.dbaelz.siloshare.feature.addnote.AddNoteViewModel
import de.dbaelz.siloshare.feature.notes.NotesViewModel
import de.dbaelz.siloshare.feature.settings.SettingsViewModel
import de.dbaelz.siloshare.network.createHttpClient
import de.dbaelz.siloshare.repository.DefaultNotesRepository
import de.dbaelz.siloshare.repository.MultiplatformSettingsRepository
import de.dbaelz.siloshare.repository.NotesRepository
import de.dbaelz.siloshare.repository.SettingsRepository
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun appModule(navHostController: NavHostController) = module {
    single<SettingsRepository> { MultiplatformSettingsRepository(Settings()) }
    single { createHttpClient(settingsRepository = get()) }

    single<NotesRepository> { DefaultNotesRepository(get()) }

    single<ActionDispatcher> { DefaultActionDispatcher(navHostController) }

    viewModelOf(::NotesViewModel)
    viewModelOf(::AddNoteViewModel)
    viewModelOf(::SettingsViewModel)
}