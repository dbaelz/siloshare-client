package de.dbaelz.siloshare.feature.addnote

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.dbaelz.siloshare.feature.addnote.AddNoteViewModelContract.Event
import de.dbaelz.siloshare.ui.ErrorText
import de.dbaelz.siloshare.ui.Loading
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddNoteScreen(onSuccess: () -> Unit) {
    val viewModel: AddNoteViewModel = koinViewModel()

    val state by viewModel.state.collectAsState()

    if (state.isSuccess) {
        onSuccess()
    } else if (state.isLoading) {
        Loading()
    } else {
        AddNoteContent(message = state.message) {
            viewModel.sendEvent(Event.SaveNote(it))
        }
    }
}

@Composable
private fun AddNoteContent(
    message: String?,
    onSave: (String) -> Unit
) {
    var noteText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        if (message != null) {
            ErrorText(message)
        }

        OutlinedTextField(
            value = noteText,
            onValueChange = { noteText = it },
            label = { Text("Note") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onSave(noteText) },
            enabled = noteText.isNotBlank(),
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Save")
        }
    }
}
