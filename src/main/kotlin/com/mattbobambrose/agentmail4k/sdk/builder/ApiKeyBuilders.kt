package com.mattbobambrose.agentmail4k.sdk.builder

import kotlinx.serialization.Serializable
import com.mattbobambrose.agentmail4k.sdk.AgentMailDsl

@AgentMailDsl
class CreateApiKeyBuilder {
  var name: String? = null

  internal fun build() = CreateApiKeyRequest(name = name)
}

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
