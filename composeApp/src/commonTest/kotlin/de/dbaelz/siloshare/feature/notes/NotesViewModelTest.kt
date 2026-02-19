package de.dbaelz.siloshare.feature.notes

import app.cash.turbine.test
import de.dbaelz.siloshare.TestActionDispatcher
import de.dbaelz.siloshare.navigation.Action
import de.dbaelz.siloshare.repository.TestNotesRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NotesViewModelTest {
    @Test
    fun notesRefresh_dispatch_updatesState() = runTest {
        val repo = TestNotesRepository(200)
        val dispatcher = TestActionDispatcher()

        val viewModel = NotesViewModel(repo, dispatcher)

        viewModel.state.test {
            val initialState = awaitItem()
            assertTrue(initialState.isLoading)
            assertEquals(emptyList(), initialState.notes)

            delay(200)
            dispatcher.dispatch(Action.NotesRefresh)

            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(1, state.notes.size)
            assertEquals("Test note", state.notes.first().text)
        }
    }
}
