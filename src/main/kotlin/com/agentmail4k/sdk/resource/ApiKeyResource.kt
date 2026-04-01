package com.agentmail4k.sdk.resource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.encodeURLPathPart
import com.agentmail4k.sdk.builder.CreateApiKeyBuilder
import com.agentmail4k.sdk.builder.ListApiKeysBuilder
import com.agentmail4k.sdk.model.ApiKeyList
import com.agentmail4k.sdk.model.CreateApiKeyResponse

/** Provides operations for managing API keys: list, create, and delete. */
class ApiKeyResource internal constructor(
  private val client: HttpClient,
  private val basePath: String,
) {
  /** Lists API keys with optional pagination. */
  suspend fun list(block: ListApiKeysBuilder.() -> Unit = {}): ApiKeyList {
    val params = ListApiKeysBuilder().apply(block).toQueryParams()
    return client.get(basePath) {
      params.forEach { (k, v) -> parameter(k, v) }
    }.body()
  }

  /** Creates a new API key. */
  suspend fun create(block: CreateApiKeyBuilder.() -> Unit = {}): CreateApiKeyResponse {
    val body = CreateApiKeyBuilder().apply(block).build()
    return client.post(basePath) {
      setBody(body)
    }.body()
  }

  /** Deletes an API key by ID. */
  suspend fun delete(apiKeyId: String) {
    require(apiKeyId.isNotEmpty()) { "API key ID must not be empty." }
    client.delete("$basePath/${apiKeyId.encodeURLPathPart()}")
  }
}
