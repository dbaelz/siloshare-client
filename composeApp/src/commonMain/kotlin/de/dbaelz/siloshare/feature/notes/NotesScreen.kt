package de.dbaelz.siloshare.feature.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.dbaelz.siloshare.repository.NotesRepository
import de.dbaelz.siloshare.ui.Loading
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NotesScreen() {
    val viewModel: NotesViewModel = koinViewModel()

    val state by viewModel.state.collectAsState()

    if (state.isLoading) {
        Loading()
    } else if (state.message != null) {
        Text(text = state.message ?: "An error occurred")
    }

    NotesContent(message = state.message, notes = state.notes)
}

@Composable
private fun NotesContent(
    message: String? = null,
    notes: List<NotesRepository.Note>
) {
    LazyColumn {
        if (message != null) {
            stickyHeader {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .background(MaterialTheme.colorScheme.errorContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
        }

        items(notes) { note ->
            Text(text = note.text)
        }
    }
}
