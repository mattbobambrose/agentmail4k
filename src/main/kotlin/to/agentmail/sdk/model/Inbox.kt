package to.agentmail.sdk.model

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Inbox(
    @SerialName("pod_id") val podId: String? = null,
    @SerialName("inbox_id") val inboxId: String,
    val email: String,
    @SerialName("display_name") val displayName: String? = null,
    @SerialName("client_id") val clientId: String? = null,
    @SerialName("updated_at") val updatedAt: Instant,
    @SerialName("created_at") val createdAt: Instant,
)

@Serializable
data class InboxList(
    val count: Int,
    val limit: Int? = null,
    @SerialName("next_page_token") val nextPageToken: String? = null,
    val inboxes: List<Inbox>,
)
