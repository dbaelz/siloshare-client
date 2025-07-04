package de.dbaelz.siloshare.repository

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

class NotesRepository(private val httpClient: HttpClient) {
    suspend fun getNotes(): List<Note> {
        return httpClient.get(NOTES_ENDPOINT).body()
    }

    suspend fun addNote(text: String): String {
        return httpClient.post(NOTES_ENDPOINT) {
            contentType(ContentType.Application.Json)
            setBody(AddNote(text))
        }.body()
    }

    @Serializable
    data class Note(val id: String, val timestamp: Instant, val text: String)

    @Serializable
    data class AddNote(val text: String)

    private companion object {
        const val NOTES_ENDPOINT = "notes"
    }
}