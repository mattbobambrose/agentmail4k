package com.mattbobambrose.agentmail4k.sdk.builder

import kotlinx.serialization.Serializable
import com.mattbobambrose.agentmail4k.sdk.AgentMailDsl

/** DSL builder for creating a new allow/block list entry. Requires an entry value. */
@AgentMailDsl
class CreateListEntryBuilder {
  var entry: String? = null

  internal fun build(): CreateListEntryRequest {
    requireNotNull(entry) { "List entry value is required" }
    return CreateListEntryRequest(entry = entry!!)
  }
}

/** DSL builder for configuring list entry pagination. */
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
