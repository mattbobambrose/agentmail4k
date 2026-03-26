package com.mattbobambrose.agentmail4k.sdk.builder

import com.mattbobambrose.agentmail4k.sdk.AgentMailDsl

@AgentMailDsl
class CreatePodBuilder {
  internal fun build() = Unit
}

@AgentMailDsl
class ListPodsBuilder {
  var limit: Int? = null
  var pageToken: String? = null
  var ascending: Boolean? = null

  internal fun toQueryParams(): Map<String, String> = buildMap {
    limit?.let { put("limit", it.toString()) }
    pageToken?.let { put("page_token", it) }
    ascending?.let { put("ascending", it.toString()) }
  }
}
