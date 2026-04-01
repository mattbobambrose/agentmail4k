package com.agentmail4k.sdk.internal

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import com.agentmail4k.sdk.AgentMailConfig

/**
 * Factory for creating a configured Ktor [HttpClient] with JSON content negotiation,
 * bearer auth, timeouts, retry, and base URL defaults.
 */
internal object HttpClientFactory {

  /** Creates a configured Ktor [HttpClient] from the given [AgentMailConfig]. */
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
