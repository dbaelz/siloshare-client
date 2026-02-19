package de.dbaelz.siloshare.repository

import de.dbaelz.siloshare.repository.NotesRepository.Note
import de.dbaelz.siloshare.repository.NotesRepository.Checklist
import de.dbaelz.siloshare.repository.NotesRepository.ChecklistItem
import kotlinx.datetime.Instant

class TestNotesRepository : NotesRepository {
    override suspend fun getNotes(): List<Note> =
        listOf(
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

    override suspend fun addNote(text: String): String = "1"

    override suspend fun deleteNote(id: String): Boolean {
        return true
    }
}