package de.dbaelz.siloshare.feature.notes

import androidx.lifecycle.viewModelScope
import de.dbaelz.siloshare.feature.BaseViewModel
import de.dbaelz.siloshare.feature.notes.NotesViewModel.InternalEvent
import de.dbaelz.siloshare.feature.notes.NotesViewModelContract.Event
import de.dbaelz.siloshare.feature.notes.NotesViewModelContract.State
import de.dbaelz.siloshare.repository.NotesRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class NotesViewModel(
    private val notesRepository: NotesRepository
) : BaseViewModel<State, Event, InternalEvent>(
    initialState = State(isLoading = true)
) {

    init {
        viewModelScope.launch {
            event.map { reduce(state.value, it) }.collect { updateState(it) }
        }

        getNotes()
    }

    private fun reduce(state: State, event: Event): State {
        return when (event) {
            is InternalEvent.GetNodesSuccess -> {
                State(
                    notes = event.notes,
                    isLoading = false
                )
            }

            is InternalEvent.GetNodesError -> {
                state.copy(
                    isLoading = false,
                    message = event.error.toString()
                )
            }
        }
    }

    private fun getNotes() {
        viewModelScope.launch {
            try {
                val notes = notesRepository.getNotes().sortedByDescending { it.timestamp }
                sendEvent(InternalEvent.GetNodesSuccess(notes))
            } catch (exception: Exception) {
                sendEvent(InternalEvent.GetNodesError(exception))
            }
        }
    }

    sealed interface InternalEvent : Event {
        data class GetNodesSuccess(val notes: List<NotesRepository.Note>) : InternalEvent
        data class GetNodesError(val error: Throwable) : InternalEvent
    }
}

