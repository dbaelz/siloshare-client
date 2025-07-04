package de.dbaelz.siloshare.network

import de.dbaelz.siloshare.repository.SettingsRepository
import io.github.aakira.napier.Napier
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json


fun createHttpClient(
    settingsRepository: SettingsRepository,
    engine: HttpClientEngine? = null
): HttpClient {
    val clientConfig: HttpClientConfig<*>.() -> Unit = {
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Napier.d(message = message, tag = "HttpClient")
                }
            }
            level = LogLevel.ALL
        }

        install(Auth) {
            basic {
                credentials {
                    BasicAuthCredentials(
                        username = settingsRepository.getUsername(),
                        password = settingsRepository.getPassword()
                    )
                }
            }
        }
        install(ContentNegotiation) {
            json(Json {
                encodeDefaults = true
                isLenient = true
                coerceInputValues = true
                ignoreUnknownKeys = true
            })
        }

        defaultRequest {
            url {
                host = settingsRepository.getHostAddress()
                port = settingsRepository.getPort()
                path("/api/")
            }
        }
    }

    return if (engine == null) {
        HttpClient(clientConfig)
    } else {
        HttpClient(engine, clientConfig)
    }
}