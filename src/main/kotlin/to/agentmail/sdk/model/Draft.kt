package to.agentmail.sdk.model

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Draft(
    @SerialName("inbox_id") val inboxId: String,
    @SerialName("draft_id") val draftId: String,
    val labels: List<String> = emptyList(),
    val timestamp: Instant,
    val from: String? = null,
    val to: List<String> = emptyList(),
    val cc: List<String> = emptyList(),
    val bcc: List<String> = emptyList(),
    val subject: String? = null,
    val preview: String? = null,
    val text: String? = null,
    val html: String? = null,
    val attachments: List<Attachment> = emptyList(),
    @SerialName("updated_at") val updatedAt: Instant,
    @SerialName("created_at") val createdAt: Instant,
)

@Serializable
data class DraftList(
    val count: Int,
    val limit: Int? = null,
    @SerialName("next_page_token") val nextPageToken: String? = null,
    val drafts: List<Draft>,
)
