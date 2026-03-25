package to.agentmail.sdk.builder

import kotlinx.serialization.Serializable
import to.agentmail.sdk.AgentMailDsl

@AgentMailDsl
class CreateDomainBuilder {
  var name: String? = null

  internal fun build(): CreateDomainRequest {
    requireNotNull(name) { "Domain name is required" }
    return CreateDomainRequest(name = name!!)
  }
}

@AgentMailDsl
class UpdateDomainBuilder {
  var name: String? = null

  internal fun build() = UpdateDomainRequest(name = name)
}

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
