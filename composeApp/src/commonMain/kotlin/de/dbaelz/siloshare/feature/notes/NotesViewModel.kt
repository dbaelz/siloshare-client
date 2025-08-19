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

            is Event.Delete -> {
                deleteNote(event.id)
                state.copy(isLoading = true, message = null)
            }

            is InternalEvent.DeleteNoteError -> {
                state.copy(isLoading =  false, message = "Error deleting note ${event.id}")
            }

            is InternalEvent.DeleteNoteSuccess -> {
                state.copy(isLoading =  false, message = "Note ${event.id} deleted")
            }

        }
    }

    private fun getNotes() {
        viewModelScope.launch {
            try {
                val notes = notesRepository.getNotes().sortedByDescending { it.timestamp }
                sendEvent(InternalEvent.GetNotesSuccess(notes))
            } catch (exception: Exception) {
                sendEvent(InternalEvent.GetNotesError(exception))
            }
        }
    }

    private fun deleteNote(id: String) {
        viewModelScope.launch {
            try {
                notesRepository.deleteNote(id)
                sendEvent(InternalEvent.DeleteNoteSuccess(id))
            } catch (exception: Exception) {
                sendEvent(InternalEvent.DeleteNoteError(id))
            }
        }
    }

    sealed interface InternalEvent : Event {
        data class GetNotesSuccess(val notes: List<NotesRepository.Note>) : InternalEvent
        data class GetNotesError(val error: Throwable) : InternalEvent

        data class DeleteNoteSuccess(val id: String) : InternalEvent
        data class DeleteNoteError(val id: String) : InternalEvent
    }
}

