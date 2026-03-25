package to.agentmail.sdk.model

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Domain(
  @SerialName("domain_id") val domainId: String,
  val name: String,
  val verified: Boolean,
  @SerialName("updated_at") val updatedAt: Instant,
  @SerialName("created_at") val createdAt: Instant,
)

@Serializable
data class DomainList(
  val count: Int,
  val limit: Int? = null,
  @SerialName("next_page_token") val nextPageToken: String? = null,
  val domains: List<Domain>,
)
