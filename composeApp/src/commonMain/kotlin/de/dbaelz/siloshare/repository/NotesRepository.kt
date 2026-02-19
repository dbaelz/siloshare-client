package de.dbaelz.siloshare.repository

import de.dbaelz.siloshare.repository.NotesRepository.Note
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

interface NotesRepository {
    suspend fun getNotes(): List<Note>
    suspend fun addNote(text: String): String
    suspend fun deleteNote(id: String): Boolean

    @Serializable
    data class Note(
        val id: String,
        val timestamp: Instant,
        val text: String,
        val checklist: Checklist? = null
    )

    @Serializable
    data class AddNote(val text: String)

    @Serializable
    data class ChecklistItem(val id: String, val text: String, val done: Boolean = false)

    @Serializable
    data class Checklist(
        val items: List<ChecklistItem> = emptyList(),
        val updatedAt: Instant? = null
    )
}

class DefaultNotesRepository(private val httpClient: HttpClient) : NotesRepository {
    override suspend fun getNotes(): List<Note> {
        return httpClient.get(NOTES_ENDPOINT).body()
    }

    override suspend fun addNote(text: String): String {
        return httpClient.post(NOTES_ENDPOINT) {
            contentType(ContentType.Application.Json)
            setBody(NotesRepository.AddNote(text))
        }.body()
    }

    override suspend fun deleteNote(id: String): Boolean {
        return httpClient.delete("$NOTES_ENDPOINT/$id").status == HttpStatusCode.NoContent
    }

    private companion object {
        const val NOTES_ENDPOINT = "notes"
    }
}