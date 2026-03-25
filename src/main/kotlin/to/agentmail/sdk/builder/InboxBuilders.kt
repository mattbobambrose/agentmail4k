package to.agentmail.sdk.builder

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import to.agentmail.sdk.AgentMailDsl

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

@AgentMailDsl
class UpdateInboxBuilder {
  var displayName: String? = null

  internal fun build(): UpdateInboxRequest {
    requireNotNull(displayName) { "displayName is required for updating an inbox" }
    return UpdateInboxRequest(displayName = displayName!!)
  }
}

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
