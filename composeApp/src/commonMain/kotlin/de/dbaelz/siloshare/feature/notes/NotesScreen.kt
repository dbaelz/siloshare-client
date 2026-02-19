package de.dbaelz.siloshare.feature.notes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.dbaelz.siloshare.repository.NotesRepository
import de.dbaelz.siloshare.ui.ErrorText
import de.dbaelz.siloshare.ui.Loading
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NotesScreen() {
    val viewModel: NotesViewModel = koinViewModel()

    val state by viewModel.state.collectAsState()

    if (state.isLoading) {
        Loading()
    } else {
        NotesContent(
            message = state.message,
            notes = state.notes,
            dirtyNoteIds = state.dirtyNoteIds,
            savingNoteIds = state.savingNoteIds,
            onDeleteClick = { viewModel.sendEvent(NotesViewModelContract.Event.Delete(it)) },
            onChecklistTextChanged = { noteId, itemId, text ->
                viewModel.sendEvent(
                    NotesViewModelContract.Event.UpdateChecklistItemText(noteId, itemId, text)
                )
            },
            onChecklistToggle = { noteId, itemId ->
                viewModel.sendEvent(
                    NotesViewModelContract.Event.ToggleChecklistItem(
                        noteId,
                        itemId
                    )
                )
            },
            onChecklistAdd = { noteId, text ->
                viewModel.sendEvent(
                    NotesViewModelContract.Event.AddChecklistItem(
                        noteId,
                        text
                    )
                )
            },
            onChecklistDelete = { noteId, itemId ->
                viewModel.sendEvent(
                    NotesViewModelContract.Event.DeleteChecklistItem(
                        noteId,
                        itemId
                    )
                )
            },
            onSaveChecklist = { noteId ->
                viewModel.sendEvent(
                    NotesViewModelContract.Event.SaveChecklist(
                        noteId
                    )
                )
            },
            onRevertChecklist = { noteId ->
                viewModel.sendEvent(
                    NotesViewModelContract.Event.RevertChecklistEdits(
                        noteId
                    )
                )
            }
        )
    }

}

@Composable
private fun NotesContent(
    message: String? = null,
    notes: List<NotesRepository.Note>,
    dirtyNoteIds: Set<String>,
    savingNoteIds: Set<String>,
    onDeleteClick: (id: String) -> Unit,
    onChecklistTextChanged: (noteId: String, itemId: String, text: String) -> Unit,
    onChecklistToggle: (noteId: String, itemId: String) -> Unit,
    onChecklistAdd: (noteId: String, text: String) -> Unit,
    onChecklistDelete: (noteId: String, itemId: String) -> Unit,
    onSaveChecklist: (noteId: String) -> Unit,
    onRevertChecklist: (noteId: String) -> Unit
) {
    LazyColumn {
        if (message != null) {
            stickyHeader {
                ErrorText(message)
            }
        }

        items(notes) { note ->
            NoteCard(
                note = note,
                isDirty = dirtyNoteIds.contains(note.id),
                isSaving = savingNoteIds.contains(note.id),
                onDeleteClick = onDeleteClick,
                onChecklistTextChanged = onChecklistTextChanged,
                onChecklistToggle = onChecklistToggle,
                onChecklistAdd = onChecklistAdd,
                onChecklistDelete = onChecklistDelete,
                onSaveChecklist = onSaveChecklist,
                onRevertChecklist = onRevertChecklist
            )
        }
    }
}

@OptIn(FormatStringsInDatetimeFormats::class)
@Composable
private fun NoteCard(
    note: NotesRepository.Note,
    isDirty: Boolean,
    isSaving: Boolean,
    onDeleteClick: (String) -> Unit,
    onChecklistTextChanged: (noteId: String, itemId: String, text: String) -> Unit,
    onChecklistToggle: (noteId: String, itemId: String) -> Unit,
    onChecklistAdd: (noteId: String, text: String) -> Unit,
    onChecklistDelete: (noteId: String, itemId: String) -> Unit,
    onSaveChecklist: (noteId: String) -> Unit,
    onRevertChecklist: (noteId: String) -> Unit
) {
    val clipboardManager = LocalClipboardManager.current

    var checklistExpanded by remember { mutableStateOf(false) }
    var newItemText by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(modifier = Modifier.padding(12.dp)) {
            Column {
                Text(
                    text = note.text,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = note.timestamp.toLocalDateTime(TimeZone.currentSystemDefault())
                        .format(LocalDateTime.Format { byUnicodePattern("yyyy-MM-dd HH:mm") }),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = note.id,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                val itemsList = note.checklist?.items ?: emptyList()
                val hasNonBlankItems = note.checklist?.items?.any { it.text.isNotBlank() } ?: false

                if (itemsList.isNotEmpty() || checklistExpanded) {
                    if (itemsList.isNotEmpty()) Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(text = "Checklist", fontWeight = FontWeight.Bold)
                        IconButton(onClick = { checklistExpanded = !checklistExpanded }) {
                            Icon(
                                imageVector = if (checklistExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = if (checklistExpanded) "Collapse checklist" else "Expand checklist"
                            )
                        }
                    }

                    if (checklistExpanded) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            if (itemsList.isNotEmpty()) {
                                itemsList.forEach { item ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Start
                                    ) {
                                        Checkbox(
                                            checked = item.done,
                                            onCheckedChange = {
                                                onChecklistToggle(
                                                    note.id,
                                                    item.id
                                                )
                                            }
                                        )

                                        Spacer(modifier = Modifier.width(8.dp))

                                        OutlinedTextField(
                                            value = item.text,
                                            onValueChange = {
                                                onChecklistTextChanged(
                                                    note.id,
                                                    item.id,
                                                    it
                                                )
                                            },
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(end = 8.dp)
                                        )

                                        IconButton(onClick = {
                                            onChecklistDelete(
                                                note.id,
                                                item.id
                                            )
                                        }) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Delete checklist item"
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                OutlinedTextField(
                                    value = newItemText,
                                    onValueChange = { newItemText = it },
                                    modifier = Modifier.weight(1f),
                                    placeholder = { Text("New item") }
                                )

                                IconButton(onClick = {
                                    if (newItemText.isNotBlank()) {
                                        onChecklistAdd(note.id, newItemText)
                                        newItemText = ""
                                    }
                                }) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = "Add checklist item"
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                if (isSaving) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                } else {
                                    TextButton(onClick = { onRevertChecklist(note.id) }) {
                                        Text("Undo")
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Button(
                                        enabled = hasNonBlankItems || newItemText.isNotBlank(),
                                        onClick = {
                                            if (newItemText.isNotBlank()) {
                                                onChecklistAdd(note.id, newItemText)
                                                // clear local pending text immediately so UI reflects the action
                                                newItemText = ""
                                            }
                                            onSaveChecklist(note.id)
                                        }
                                    ) {
                                        Text("Save")
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { checklistExpanded = true }) {
                            Text("Add checklist")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { clipboardManager.setText(AnnotatedString(note.text)) },
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(text = "Copy", fontSize = 16.sp)

                        Spacer(modifier = Modifier.width(8.dp))

                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                    }

                    IconButton(onClick = { onDeleteClick(note.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            }
        }
    }
}
