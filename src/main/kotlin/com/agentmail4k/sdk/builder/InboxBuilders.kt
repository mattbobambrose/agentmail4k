package com.agentmail4k.sdk.builder

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.agentmail4k.sdk.AgentMailDsl

/** DSL builder for creating a new inbox with optional username, domain, display name, and client ID. */
@AgentMailDsl
class CreateInboxBuilder {
  var username: String? = null
  var domain: String? = null
  var displayName: String? = null
  var clientId: String? = null

  internal fun build() = CreateInboxRequest(
    username = username,
    domain = domain,
    displayName = displayName,
    clientId = clientId,
  )
}

/** DSL builder for updating an inbox's display name. */
@AgentMailDsl
class UpdateInboxBuilder {
  var displayName: String? = null

  internal fun build(): UpdateInboxRequest {
    requireNotNull(displayName) { "displayName is required for updating an inbox" }
    return UpdateInboxRequest(displayName = displayName!!)
  }
}

/** DSL builder for configuring inbox list pagination. */
@AgentMailDsl
class ListInboxesBuilder {
  var limit: Int? = null
  var pageToken: String? = null
  var ascending: Boolean? = null

  internal fun toQueryParams(): Map<String, String> = buildMap {
    limit?.let { put("limit", it.toString()) }
    pageToken?.let { put("page_token", it) }
    ascending?.let { put("ascending", it.toString()) }
  }
}

@Serializable
internal data class CreateInboxRequest(
  val username: String? = null,
  val domain: String? = null,
  @SerialName("display_name") val displayName: String? = null,
  @SerialName("client_id") val clientId: String? = null,
)

@Serializable
internal data class UpdateInboxRequest(
  @SerialName("display_name") val displayName: String,
)
