package de.dbaelz.siloshare.feature.addnote

object AddNoteViewModelContract {
    data class State(
        val isSuccess: Boolean = false,
        val isLoading: Boolean = false,
        val message: String? = null
    )

    sealed interface Event {
        data class SaveNote(val text: String) : Event
    }
}

