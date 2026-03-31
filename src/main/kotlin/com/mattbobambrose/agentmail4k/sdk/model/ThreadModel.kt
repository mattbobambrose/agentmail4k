package com.mattbobambrose.agentmail4k.sdk.model

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Thread(
  @SerialName("inbox_id") val inboxId: String,
  @SerialName("thread_id") val threadId: String,
  val labels: List<String> = emptyList(),
  val timestamp: Instant,
  @SerialName("received_timestamp") val receivedTimestamp: Instant? = null,
  @SerialName("sent_timestamp") val sentTimestamp: Instant? = null,
  val senders: List<String> = emptyList(),
  val recipients: List<String> = emptyList(),
  val subject: String? = null,
  val preview: String? = null,
  val attachments: List<Attachment> = emptyList(),
  @SerialName("last_message_id") val lastMessageId: String,
  @SerialName("message_count") val messageCount: Int,
  val size: Int,
  @SerialName("updated_at") val updatedAt: Instant,
  @SerialName("created_at") val createdAt: Instant,
)

@Serializable
data class ThreadList(
  val count: Int,
  val limit: Int? = null,
  @SerialName("next_page_token") val nextPageToken: String? = null,
  val threads: List<Thread>,
)
