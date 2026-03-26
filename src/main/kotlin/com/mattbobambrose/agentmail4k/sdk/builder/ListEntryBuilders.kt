package com.mattbobambrose.agentmail4k.sdk.builder

import kotlinx.serialization.Serializable
import com.mattbobambrose.agentmail4k.sdk.AgentMailDsl

@AgentMailDsl
class CreateListEntryBuilder {
  var entry: String? = null

  internal fun build(): CreateListEntryRequest {
    requireNotNull(entry) { "List entry value is required" }
    return CreateListEntryRequest(entry = entry!!)
  }
}

@AgentMailDsl
class ListEntriesBuilder {
  var limit: Int? = null
  var pageToken: String? = null

  internal fun toQueryParams(): Map<String, String> = buildMap {
    limit?.let { put("limit", it.toString()) }
    pageToken?.let { put("page_token", it) }
  }
}

@Serializable
internal data class CreateListEntryRequest(val entry: String)
