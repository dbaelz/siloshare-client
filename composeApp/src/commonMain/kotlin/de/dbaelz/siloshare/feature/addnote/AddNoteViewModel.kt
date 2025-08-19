package de.dbaelz.siloshare.feature.addnote

import androidx.lifecycle.viewModelScope
import de.dbaelz.siloshare.feature.BaseViewModel
import de.dbaelz.siloshare.feature.addnote.AddNoteViewModel.InternalEvent
import de.dbaelz.siloshare.feature.addnote.AddNoteViewModelContract.Event
import de.dbaelz.siloshare.feature.addnote.AddNoteViewModelContract.State
import de.dbaelz.siloshare.repository.NotesRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class AddNoteViewModel(
    private val notesRepository: NotesRepository
) : BaseViewModel<State, Event, InternalEvent>(
    initialState = State()
) {

    init {
        viewModelScope.launch {
            event.map { reduce(state.value, it) }.collect { updateState(it) }
        }
    }

    private fun reduce(state: State, event: Event): State {
        return when (event) {
            is InternalEvent.AddNoteSuccess -> {
                state.copy(isSuccess = true, isLoading = false, message = null)
            }

            is InternalEvent.AddNoteError -> {
                state.copy(
                    isLoading = false,
                    message = event.error.toString()
                )
            }

            is Event.SaveNote -> {
                saveNote(event.text)
                state.copy(isLoading = true, message = null)
            }
        }
    }

    private fun saveNote(text: String) {
        viewModelScope.launch {
            try {
                val newNote = notesRepository.addNote(text)
                sendEvent(InternalEvent.AddNoteSuccess(newNote))
            } catch (exception: Exception) {
                sendEvent(InternalEvent.AddNoteError(exception))
            }
        }
    }

    sealed interface InternalEvent : Event {
        data class AddNoteSuccess(val note: String) : InternalEvent
        data class AddNoteError(val error: Throwable) : InternalEvent
    }
}

