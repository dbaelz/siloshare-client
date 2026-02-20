package de.dbaelz.siloshare.feature.notes

import de.dbaelz.siloshare.repository.NotesRepository

object NotesViewModelContract {
    data class State(
        val notes: List<NotesRepository.Note> = emptyList(),
        val isLoading: Boolean = false,
        val message: String? = null,
        val dirtyNoteIds: Set<String> = emptySet(),
        val savingNoteIds: Set<String> = emptySet(),
        val originalChecklists: Map<String, NotesRepository.Checklist?> = emptyMap()
    )

    sealed interface Event {
        data class Delete(val id: String) : Event
        data class DeleteChecklist(val noteId: String) : Event

        data class UpdateChecklistItemText(val noteId: String, val itemId: String, val text: String) : Event
        data class ToggleChecklistItem(val noteId: String, val itemId: String) : Event
        data class AddChecklistItem(val noteId: String, val text: String) : Event
        data class DeleteChecklistItem(val noteId: String, val itemId: String) : Event

        data class SaveChecklist(val noteId: String) : Event
        data class RevertChecklistEdits(val noteId: String) : Event
    }
}
