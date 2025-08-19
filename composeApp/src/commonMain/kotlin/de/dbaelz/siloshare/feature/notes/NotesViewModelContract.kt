package de.dbaelz.siloshare.feature.notes

import de.dbaelz.siloshare.repository.NotesRepository

object NotesViewModelContract {
    data class State(
        val notes: List<NotesRepository.Note> = emptyList(),
        val isLoading: Boolean = false,
        val message: String? = null
    )

    sealed interface Event {
        data class Delete(val id: String) : Event
    }
}

