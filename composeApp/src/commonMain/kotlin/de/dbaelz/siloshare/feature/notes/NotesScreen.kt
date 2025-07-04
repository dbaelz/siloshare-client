package de.dbaelz.siloshare.feature.notes

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import de.dbaelz.siloshare.repository.NotesRepository
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NotesScreen() {
    val viewModel: NotesViewModel = koinViewModel()

    val state by viewModel.state.collectAsState()

    if (state.isLoading) {
        CircularProgressIndicator()
    } else if (state.message != null) {
        Text(text = state.message ?: "An error occurred")
    } else {
        NotesContent(state.notes)
    }
}

@Composable
private fun NotesContent(notes: List<NotesRepository.Note>) {
    LazyColumn {
        items(notes) { note ->
            Text(text = note.text)
        }
    }
}
