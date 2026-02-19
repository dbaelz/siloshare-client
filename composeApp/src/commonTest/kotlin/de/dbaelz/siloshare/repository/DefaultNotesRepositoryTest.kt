package de.dbaelz.siloshare.repository

import de.dbaelz.siloshare.network.createHttpClient
import de.dbaelz.siloshare.repository.NotesRepository.Note
import de.dbaelz.siloshare.repository.NotesRepository.Checklist
import de.dbaelz.siloshare.repository.NotesRepository.ChecklistItem
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class DefaultNotesRepositoryTest {
    @Test
    fun getNotes_returnsNotesList() = runTest {
        val notes = listOf(
            Note(
                "1",
                Instant.parse("2025-08-01T00:00:00Z"),
                "Test note",
                checklist = Checklist(
                    items = listOf(
                        ChecklistItem("a", "First item", false)
                    ),
                    updatedAt = Instant.parse("2025-08-01T00:00:00Z")
                )
            ),
            Note(
                "2",
                Instant.parse("2025-08-02T00:00:00Z"),
                "Another note",
                checklist = null
            )
        )
        val mockEngine = MockEngine { _ ->
            respond(
                content = Json.encodeToString(notes),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val client = createHttpClient(settingsRepository = TestSettingsRepository(), engine = mockEngine)

        val repo = DefaultNotesRepository(client)
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

        val repo = DefaultNotesRepository(client)
        val result = repo.addNote("New note")

        assertEquals(expected.id, result)
    }

    @Test
    fun deleteNote_returnsTrueOnSuccess() = runTest {
        val mockEngine = MockEngine { request ->
            assertEquals(HttpMethod.Delete, request.method)
            respond(
                content = "",
                status = HttpStatusCode.NoContent,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        val client = createHttpClient(settingsRepository = TestSettingsRepository(), engine = mockEngine)

        val repo = DefaultNotesRepository(client)
        val result = repo.deleteNote("1")
        assertEquals(true, result)
    }

    @Test
    fun deleteNote_returnsFalseOnFailure() = runTest {
        val mockEngine = MockEngine { request ->
            assertEquals(HttpMethod.Delete, request.method)
            respond(
                content = "",
                status = HttpStatusCode.NotFound,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        val client = createHttpClient(settingsRepository = TestSettingsRepository(), engine = mockEngine)

        val repo = DefaultNotesRepository(client)
        val result = repo.deleteNote("nonexistent")
        assertEquals(false, result)
    }
}