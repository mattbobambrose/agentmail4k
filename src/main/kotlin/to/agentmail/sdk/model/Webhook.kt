package to.agentmail.sdk.model

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Webhook(
  @SerialName("webhook_id") val webhookId: String,
  val url: String,
  val events: List<String> = emptyList(),
  @SerialName("updated_at") val updatedAt: Instant,
  @SerialName("created_at") val createdAt: Instant,
)

@Serializable
data class WebhookList(
  val count: Int,
  val limit: Int? = null,
  @SerialName("next_page_token") val nextPageToken: String? = null,
  val webhooks: List<Webhook>,
)

enum class WebhookEvent(val value: String) {
  MESSAGE_RECEIVED("message.received"),
  MESSAGE_SENT("message.sent"),
  MESSAGE_DELIVERED("message.delivered"),
  MESSAGE_BOUNCED("message.bounced"),
  MESSAGE_COMPLAINED("message.complained"),
  MESSAGE_REJECTED("message.rejected"),
  DOMAIN_VERIFIED("domain.verified"),
}
