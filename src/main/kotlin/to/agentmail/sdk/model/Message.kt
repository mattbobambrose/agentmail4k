package to.agentmail.sdk.model

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import to.agentmail.sdk.AgentMail

@Serializable
data class Message(
  @SerialName("inbox_id") val inboxId: String,
  @SerialName("thread_id") val threadId: String,
  @SerialName("message_id") val messageId: String,
  val labels: List<String> = emptyList(),
  val timestamp: Instant,
  val from: String,
  val to: List<String> = emptyList(),
  val cc: List<String> = emptyList(),
  val bcc: List<String> = emptyList(),
  val subject: String? = null,
  val preview: String? = null,
  val text: String? = null,
  val html: String? = null,
  val attachments: List<Attachment> = emptyList(),
  @SerialName("in_reply_to") val inReplyTo: String? = null,
  val references: List<String> = emptyList(),
  val headers: Map<String, String> = emptyMap(),
  val size: Int,
  @SerialName("updated_at") val updatedAt: Instant,
  @SerialName("created_at") val createdAt: Instant,
) {
  suspend fun fullMessage(client: AgentMail) = client.inboxes(inboxId).messages.get(messageId)
}

@Serializable
data class MessageList(
  val count: Int,
  val limit: Int? = null,
  @SerialName("next_page_token") val nextPageToken: String? = null,
  val messages: List<Message>,
)

@Serializable
data class SendMessageResponse(
  @SerialName("message_id") val messageId: String,
  @SerialName("thread_id") val threadId: String,
)

@Serializable
data class RawMessageResponse(
  val raw: String,
)
