package de.dbaelz.siloshare.feature.notes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
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
            onDeleteClick = { viewModel.sendEvent(NotesViewModelContract.Event.Delete(it)) }
        )
    }

}

@Composable
private fun NotesContent(
    message: String? = null,
    notes: List<NotesRepository.Note>,
    onDeleteClick: (id: String) -> Unit
) {
    LazyColumn {
        if (message != null) {
            stickyHeader {
                ErrorText(message)
            }
        }

        items(notes) { note ->
            NoteCard(note = note, onDeleteClick = onDeleteClick)
        }
    }
}

@OptIn(FormatStringsInDatetimeFormats::class)
@Composable
private fun NoteCard(
    note: NotesRepository.Note,
    onDeleteClick: (String) -> Unit
) {
    val clipboardManager = LocalClipboardManager.current

    var checklistExpanded by remember { mutableStateOf(false) }

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

                note.checklist?.items?.let { items ->
                    if (items.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))

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
                                items.forEach { item ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Start
                                    ) {
                                        Checkbox(
                                            checked = item.done,
                                            onCheckedChange = null,
                                            enabled = false
                                        )

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Text(
                                            text = item.text,
                                            modifier = Modifier.weight(1f),
                                            textDecoration = if (item.done) TextDecoration.LineThrough else TextDecoration.None,
                                            color = if (item.done) Color.Gray else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
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
