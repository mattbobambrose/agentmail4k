package com.mattbobambrose.agentmail4k.sdk.builder

import kotlinx.serialization.Serializable
import com.mattbobambrose.agentmail4k.sdk.AgentMailDsl

/** DSL builder for creating a new API key with an optional name. */
@AgentMailDsl
class CreateApiKeyBuilder {
  var name: String? = null

  internal fun build() = CreateApiKeyRequest(name = name)
}

/** DSL builder for configuring API key list pagination. */
@AgentMailDsl
class ListApiKeysBuilder {
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
internal data class CreateApiKeyRequest(val name: String? = null)
