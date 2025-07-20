package de.dbaelz.siloshare.feature.addnote

import app.cash.turbine.test
import de.dbaelz.siloshare.repository.NotesRepository
import de.dbaelz.siloshare.repository.TestNotesRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AddNoteViewModelTest {
    @Test
    fun saveNote_event_updatesState() = runTest {
        val repo = TestNotesRepository()
        val viewModel = AddNoteViewModel(repo)

        viewModel.state.test {
            val initialState = awaitItem()
            assertFalse(initialState.isSuccess)
            assertFalse(initialState.isLoading)
            assertEquals(null, initialState.message)

            val noteText = "My new note"
            viewModel.sendEvent(AddNoteViewModelContract.Event.SaveNote(noteText))

            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            val successState = awaitItem()
            assertTrue(successState.isSuccess)
            assertFalse(successState.isLoading)
            assertEquals(null, successState.message)
        }
    }
}

