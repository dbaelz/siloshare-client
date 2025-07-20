package de.dbaelz.siloshare.repository

import de.dbaelz.siloshare.network.createHttpClient
import de.dbaelz.siloshare.repository.NotesRepository.Note
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class NotesRepositoryTest {
    @Test
    fun getNotes_returnsNotesList() = runTest {
        val notes = listOf(
            Note("1", Instant.parse("2025-08-01T00:00:00Z"), "Test note"),
            Note("2", Instant.parse("2025-08-02T00:00:00Z"), "Another note")
        )
        val mockEngine = MockEngine { request ->
            respond(
                content = Json.encodeToString(notes),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val client = createHttpClient(settingsRepository = TestSettingsRepository(), engine = mockEngine)

        val repo = NotesRepository(client)
        val result = repo.getNotes()

        assertEquals(notes, result)
    }

    @Test
    fun addNote_returnsNoteId() = runTest {
        val expected = Note("123", Instant.parse("2025-08-01T00:00:00Z"), "New note")
        val mockEngine = MockEngine { request ->
            assertEquals(HttpMethod.Post, request.method)
            assertEquals(ContentType.Application.Json, request.body.contentType)

            respond(
                content = expected.id,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val client = createHttpClient(settingsRepository = TestSettingsRepository(), engine = mockEngine)

        val repo = NotesRepository(client)
        val result = repo.addNote("New note")

        assertEquals(expected.id, result)
    }
}