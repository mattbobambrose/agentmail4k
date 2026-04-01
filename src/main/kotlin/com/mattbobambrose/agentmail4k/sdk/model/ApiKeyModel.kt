package com.mattbobambrose.agentmail4k.sdk.model

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Represents an API key with its ID, optional name, and timestamps. */
@Serializable
data class ApiKey(
  @SerialName("api_key_id") val apiKeyId: String,
  val name: String? = null,
  @SerialName("updated_at") val updatedAt: Instant,
  @SerialName("created_at") val createdAt: Instant,
)

/** Paginated list of API keys. */
@Serializable
data class ApiKeyList(
  val count: Int,
  val limit: Int? = null,
  @SerialName("next_page_token") val nextPageToken: String? = null,
  @SerialName("api_keys") val apiKeys: List<ApiKey>,
)

/** Response from creating an API key, containing the key ID and secret value. */
@Serializable
data class CreateApiKeyResponse(
  @SerialName("api_key_id") val apiKeyId: String,
  @SerialName("api_key") val apiKey: String,
)
