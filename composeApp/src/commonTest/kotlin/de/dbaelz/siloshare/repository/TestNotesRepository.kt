package de.dbaelz.siloshare.repository

import de.dbaelz.siloshare.repository.NotesRepository.Note
import kotlinx.datetime.Instant

class TestNotesRepository : NotesRepository {
    override suspend fun getNotes(): List<NotesRepository.Note> =
        listOf(
            Note("1", Instant.parse("2025-08-01T00:00:00Z"), "Test note"),
        )

    override suspend fun addNote(text: String): String = "1"

    override suspend fun deleteNote(id: String): Boolean {
        return true
    }
}