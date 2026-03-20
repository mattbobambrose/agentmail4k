package to.agentmail.sdk.internal

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import to.agentmail.sdk.AgentMailConfig

internal object HttpClientFactory {

    fun create(config: AgentMailConfig): HttpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                encodeDefaults = false
                explicitNulls = false
            })
        }

        install(Auth) {
            bearer {
                loadTokens {
                    BearerTokens(config.apiKey, "")
                }
            }
        }

        install(HttpTimeout) {
            connectTimeoutMillis = config.timeout.connect.inWholeMilliseconds
            requestTimeoutMillis = config.timeout.request.inWholeMilliseconds
            socketTimeoutMillis = config.timeout.socket.inWholeMilliseconds
        }

        install(HttpRequestRetry) {
            retryOnServerErrors(maxRetries = config.retry.maxRetries)
            exponentialDelay()
        }

        defaultRequest {
            url(config.baseUrl + "/")
            contentType(ContentType.Application.Json)
        }
    }
}
