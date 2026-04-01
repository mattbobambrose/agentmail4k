package com.agentmail4k.sdk.builder

import kotlinx.serialization.Serializable
import com.agentmail4k.sdk.AgentMailDsl

/** DSL builder for creating a custom domain. Requires a domain name. */
@AgentMailDsl
class CreateDomainBuilder {
  var name: String? = null

  internal fun build(): CreateDomainRequest {
    requireNotNull(name) { "Domain name is required" }
    return CreateDomainRequest(name = name!!)
  }
}

/** DSL builder for updating a custom domain's name. */
@AgentMailDsl
class UpdateDomainBuilder {
  var name: String? = null

  internal fun build() = UpdateDomainRequest(name = name)
}

/** DSL builder for configuring domain list pagination. */
@AgentMailDsl
class ListDomainsBuilder {
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
internal data class CreateDomainRequest(val name: String)

@Serializable
internal data class UpdateDomainRequest(val name: String? = null)
