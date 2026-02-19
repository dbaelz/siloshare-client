package de.dbaelz.siloshare.repository

import de.dbaelz.siloshare.repository.NotesRepository.*
import kotlinx.coroutines.delay
import kotlinx.datetime.Instant

class TestNotesRepository(val delayMillis: Long = 0L) : NotesRepository {
    override suspend fun getNotes(): List<Note> {
        delay(delayMillis)

        return listOf(
            Note(
                "1",
                Instant.parse("2026-01-18T00:00:00Z"),
                "Test note",
                checklist = Checklist(
                    items = listOf(
                        ChecklistItem("a", "First item", false),
                        ChecklistItem("b", "Second item", true)
                    ),
                    updatedAt = Instant.parse("2026-01-18T00:00:00Z")
                )
            ),
        )
    }

    override suspend fun addNote(text: String): String {
        delay(delayMillis)
        return "1"
    }

    override suspend fun deleteNote(id: String): Boolean {
        delay(delayMillis)
        return true
    }

    override suspend fun updateChecklist(noteId: String, items: List<String>): Note {
        delay(delayMillis)
        val checklist = Checklist(
            items = items.mapIndexed { idx, text -> ChecklistItem("item-$idx", text, false) },
            updatedAt = Instant.parse("2026-01-18T00:00:00Z")
        )
        return Note(
            noteId,
            Instant.parse("2026-01-18T00:00:00Z"),
            "Test note",
            checklist = checklist
        )
    }

    override suspend fun deleteChecklist(noteId: String): Boolean {
        delay(delayMillis)
        return true
    }
}