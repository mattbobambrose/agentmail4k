package com.mattbobambrose.agentmail4k.sdk.builder

import com.mattbobambrose.agentmail4k.sdk.AgentMailDsl

/** DSL builder for creating a new pod (no configuration needed). */
@AgentMailDsl
class CreatePodBuilder {
  internal fun build() = Unit
}

/** DSL builder for configuring pod list pagination. */
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
