package de.dbaelz.siloshare.feature.notes

import androidx.lifecycle.viewModelScope
import de.dbaelz.siloshare.ActionDispatcher
import de.dbaelz.siloshare.feature.BaseViewModel
import de.dbaelz.siloshare.feature.notes.NotesViewModel.InternalEvent
import de.dbaelz.siloshare.feature.notes.NotesViewModelContract.Event
import de.dbaelz.siloshare.feature.notes.NotesViewModelContract.State
import de.dbaelz.siloshare.navigation.Action
import de.dbaelz.siloshare.repository.NotesRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.random.Random

class NotesViewModel(
    private val notesRepository: NotesRepository,
    private val actionDispatcher: ActionDispatcher
) : BaseViewModel<State, Event, InternalEvent>(
    initialState = State(isLoading = true)
) {

    init {
        viewModelScope.launch {
            event.map { reduce(state.value, it) }.collect { updateState(it) }
        }

        viewModelScope.launch {
            actionDispatcher.events.collect { action ->
                if (action is Action.NotesRefresh) {
                    getNotes()
                }
            }
        }

        getNotes()
    }

    private fun reduce(state: State, event: Event): State {
        return when (event) {
            is InternalEvent.GetNotesSuccess -> {
                State(
                    notes = event.notes,
                    isLoading = false
                )
            }

            is InternalEvent.GetNotesError -> {
                state.copy(
                    isLoading = false,
                    message = event.error.toString()
                )
            }

            is InternalEvent.SaveChecklistSuccess -> {
                handleSaveSuccess(state, event.noteId)
            }

            is InternalEvent.SaveChecklistError -> {
                handleSaveError(state, event.noteId, event.error)
            }

            is Event.Delete -> {
                deleteNote(event.id)
                state.copy(isLoading = true, message = null)
            }

            is Event.DeleteChecklist -> {
                deleteChecklist(event.noteId)
                state.copy(savingNoteIds = state.savingNoteIds + event.noteId)
            }

            is Event.UpdateChecklistItemText -> {
                val note = state.notes.find { it.id == event.noteId } ?: return state
                val original = state.originalChecklists[event.noteId] ?: note.checklist
                val newState =
                    updateChecklistItemText(state, event.noteId, event.itemId, event.text)
                newState.copy(originalChecklists = state.originalChecklists + (event.noteId to original))
            }

            is Event.ToggleChecklistItem -> {
                val note = state.notes.find { it.id == event.noteId } ?: return state
                val original = state.originalChecklists[event.noteId] ?: note.checklist
                val newState = toggleChecklistItem(state, event.noteId, event.itemId)
                newState.copy(originalChecklists = state.originalChecklists + (event.noteId to original))
            }

            is Event.AddChecklistItem -> {
                val note = state.notes.find { it.id == event.noteId } ?: return state
                val original = state.originalChecklists[event.noteId] ?: note.checklist
                val tempId = "local-${Random.nextLong().toString(16)}"
                val newState = addChecklistItem(state, event.noteId, tempId, event.text)
                newState.copy(originalChecklists = state.originalChecklists + (event.noteId to original))
            }

            is Event.DeleteChecklistItem -> {
                val note = state.notes.find { it.id == event.noteId } ?: return state
                val original = state.originalChecklists[event.noteId] ?: note.checklist
                val newState = deleteChecklistItem(state, event.noteId, event.itemId)
                newState.copy(originalChecklists = state.originalChecklists + (event.noteId to original))
            }

            is Event.SaveChecklist -> {
                saveChecklist(state, event.noteId)
                state.copy(savingNoteIds = state.savingNoteIds + event.noteId)
            }

            is Event.RevertChecklistEdits -> {
                val original = state.originalChecklists[event.noteId]
                val newNotes = state.notes.map { note ->
                    if (note.id != event.noteId) return@map note
                    note.copy(checklist = original)
                }
                state.copy(
                    notes = newNotes,
                    dirtyNoteIds = state.dirtyNoteIds - event.noteId,
                    originalChecklists = state.originalChecklists - event.noteId
                )
            }

            is InternalEvent.DeleteNoteError -> {
                state.copy(isLoading = false, message = "Error deleting note ${event.id}")
            }

            is InternalEvent.DeleteChecklistSuccess -> {
                handleSaveSuccess(state, event.noteId)
            }

            is InternalEvent.DeleteChecklistError -> {
                handleSaveError(state, event.noteId, event.error)
            }
        }
    }

    private fun getNotes() {
        viewModelScope.launch {
            try {
                val notes = notesRepository.getNotes().sortedByDescending { it.timestamp }
                sendEvent(InternalEvent.GetNotesSuccess(notes))
            } catch (_: Exception) {
                sendEvent(InternalEvent.GetNotesError(Exception("Error fetching notes")))
            }
        }
    }

    private fun deleteNote(id: String) {
        viewModelScope.launch {
            try {
                if (notesRepository.deleteNote(id)) {
                    getNotes()
                } else {
                    sendEvent(InternalEvent.DeleteNoteError(id))
                }
            } catch (_: Exception) {
                sendEvent(InternalEvent.DeleteNoteError(id))
            }
        }
    }

    private fun saveChecklist(state: State, noteId: String) {
        viewModelScope.launch {
            try {
                val note = state.notes.find { it.id == noteId } ?: return@launch
                val checklist = note.checklist ?: NotesRepository.Checklist()
                val items = checklist.items
                    .mapNotNull { item ->
                        val text = item.text.trim()
                        if (text.isNotEmpty()) {
                            NotesRepository.NewChecklistItem(text = text, done = item.done)
                        } else null
                    }

                val updatedNote = if (items.isEmpty()) {
                    if (notesRepository.deleteChecklist(noteId)) {
                        getNotes(); return@launch
                    } else {
                        throw Exception("Failed to delete checklist")
                    }
                } else {
                    notesRepository.updateChecklist(noteId, items)
                }

                val newNotes = state.notes.map { if (it.id == noteId) updatedNote else it }

                sendEvent(InternalEvent.GetNotesSuccess(newNotes))

                sendEvent(InternalEvent.SaveChecklistSuccess(noteId))
            } catch (e: Exception) {
                sendEvent(InternalEvent.SaveChecklistError(noteId, e))
            }
        }
    }

    private fun deleteChecklist(noteId: String) {
        viewModelScope.launch {
            try {
                if (notesRepository.deleteChecklist(noteId)) {
                    val notes = notesRepository.getNotes().sortedByDescending { it.timestamp }
                    sendEvent(InternalEvent.GetNotesSuccess(notes))
                    sendEvent(InternalEvent.DeleteChecklistSuccess(noteId))
                } else {
                    sendEvent(
                        InternalEvent.DeleteChecklistError(
                            noteId,
                            Exception("Failed to delete checklist")
                        )
                    )
                }
            } catch (e: Exception) {
                sendEvent(InternalEvent.DeleteChecklistError(noteId, e))
            }
        }
    }

    private fun updateChecklistItemText(
        state: State,
        noteId: String,
        itemId: String,
        text: String
    ): State {
        val newNotes = state.notes.map { note ->
            if (note.id != noteId) return@map note
            val checklist = note.checklist ?: return@map note
            val newItems =
                checklist.items.map { item -> if (item.id == itemId) item.copy(text = text) else item }
            note.copy(checklist = checklist.copy(items = newItems))
        }
        return state.copy(notes = newNotes, dirtyNoteIds = state.dirtyNoteIds + noteId)
    }

    private fun toggleChecklistItem(state: State, noteId: String, itemId: String): State {
        val newNotes = state.notes.map { note ->
            if (note.id != noteId) return@map note
            val checklist = note.checklist ?: return@map note
            val newItems =
                checklist.items.map { item -> if (item.id == itemId) item.copy(done = !item.done) else item }
            note.copy(checklist = checklist.copy(items = newItems))
        }
        return state.copy(notes = newNotes, dirtyNoteIds = state.dirtyNoteIds + noteId)
    }

    private fun addChecklistItem(
        state: State,
        noteId: String,
        tempItemId: String,
        text: String
    ): State {
        val newNotes = state.notes.map { note ->
            if (note.id != noteId) return@map note
            val checklist = note.checklist ?: NotesRepository.Checklist()
            val newItem = NotesRepository.ChecklistItem(id = tempItemId, text = text)
            note.copy(checklist = checklist.copy(items = checklist.items + newItem))
        }
        return state.copy(notes = newNotes, dirtyNoteIds = state.dirtyNoteIds + noteId)
    }

    private fun deleteChecklistItem(state: State, noteId: String, itemId: String): State {
        val newNotes = state.notes.map { note ->
            if (note.id != noteId) return@map note
            val checklist = note.checklist ?: return@map note
            val newItems = checklist.items.filter { it.id != itemId }
            note.copy(checklist = checklist.copy(items = newItems))
        }
        return state.copy(notes = newNotes, dirtyNoteIds = state.dirtyNoteIds + noteId)
    }

    private fun handleSaveSuccess(state: State, noteId: String): State {
        return state.copy(
            dirtyNoteIds = state.dirtyNoteIds - noteId,
            savingNoteIds = state.savingNoteIds - noteId,
            originalChecklists = state.originalChecklists - noteId
        )
    }

    private fun handleSaveError(state: State, noteId: String, error: Throwable): State {
        return state.copy(savingNoteIds = state.savingNoteIds - noteId, message = error.toString())
    }

    sealed interface InternalEvent : Event {
        data class GetNotesSuccess(val notes: List<NotesRepository.Note>) : InternalEvent
        data class GetNotesError(val error: Throwable) : InternalEvent

        data class DeleteNoteError(val id: String) : InternalEvent
        data class SaveChecklistSuccess(val noteId: String) : InternalEvent
        data class SaveChecklistError(val noteId: String, val error: Throwable) : InternalEvent
        data class DeleteChecklistSuccess(val noteId: String) : InternalEvent
        data class DeleteChecklistError(val noteId: String, val error: Throwable) : InternalEvent
    }
}
