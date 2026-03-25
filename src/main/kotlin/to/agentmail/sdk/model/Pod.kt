package to.agentmail.sdk.model

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Pod(
  @SerialName("pod_id") val podId: String,
  @SerialName("updated_at") val updatedAt: Instant,
  @SerialName("created_at") val createdAt: Instant,
)

@Serializable
data class PodList(
  val count: Int,
  val limit: Int? = null,
  @SerialName("next_page_token") val nextPageToken: String? = null,
  val pods: List<Pod>,
)
