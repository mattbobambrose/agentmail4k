package com.mattbobambrose.agentmail4k.sdk.resource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.encodeURLPathPart
import com.mattbobambrose.agentmail4k.sdk.builder.ForwardMessageBuilder
import com.mattbobambrose.agentmail4k.sdk.builder.ListMessagesBuilder
import com.mattbobambrose.agentmail4k.sdk.builder.ReplyAllBuilder
import com.mattbobambrose.agentmail4k.sdk.builder.ReplyBuilder
import com.mattbobambrose.agentmail4k.sdk.builder.SendMessageBuilder
import com.mattbobambrose.agentmail4k.sdk.builder.UpdateMessageBuilder
import com.mattbobambrose.agentmail4k.sdk.model.AttachmentData
import com.mattbobambrose.agentmail4k.sdk.model.Message
import com.mattbobambrose.agentmail4k.sdk.model.MessageList
import com.mattbobambrose.agentmail4k.sdk.model.RawMessageResponse
import com.mattbobambrose.agentmail4k.sdk.model.SendMessageResponse

/** Provides operations for managing email messages: list, get, update, send, reply, reply-all, forward, retrieve attachments, and get raw content. */
class MessageResource internal constructor(
  private val client: HttpClient,
  private val basePath: String,
) {
  /** Lists messages with optional pagination and filtering. */
  suspend fun list(block: ListMessagesBuilder.() -> Unit = {}): MessageList {
    val params = ListMessagesBuilder().apply(block).toQueryParams()
    return client.get(basePath) {
      params.forEach { (k, v) -> parameter(k, v) }
    }.body()
  }

  /** Retrieves a message by ID. */
  suspend fun get(messageId: String): Message {
    require(messageId.isNotEmpty()) { "Message ID must not be empty." }
    return client.get("$basePath/${messageId.encodeURLPathPart()}").body()
  }

  /** Updates a message by ID. */
  suspend fun update(messageId: String, block: UpdateMessageBuilder.() -> Unit): Message {
    require(messageId.isNotEmpty()) { "Message ID must not be empty." }
    val body = UpdateMessageBuilder().apply(block).build()
    return client.patch("$basePath/${messageId.encodeURLPathPart()}") {
      setBody(body)
    }.body()
  }

  /** Sends a new email message. */
  suspend fun send(block: SendMessageBuilder.() -> Unit): SendMessageResponse {
    val body = SendMessageBuilder().apply(block).build()
    return client.post("$basePath/send") {
      setBody(body)
    }.body()
  }

  /** Sends a reply to a specific message. */
  suspend fun reply(messageId: String, block: ReplyBuilder.() -> Unit): SendMessageResponse {
    require(messageId.isNotEmpty()) { "Message ID must not be empty." }
    val body = ReplyBuilder().apply(block).build()
    return client.post("$basePath/${messageId.encodeURLPathPart()}/reply") {
      setBody(body)
    }.body()
  }

  /** Sends a reply-all to a specific message. */
  suspend fun replyAll(messageId: String, block: ReplyAllBuilder.() -> Unit): SendMessageResponse {
    require(messageId.isNotEmpty()) { "Message ID must not be empty." }
    val body = ReplyAllBuilder().apply(block).build()
    return client.post("$basePath/${messageId.encodeURLPathPart()}/reply-all") {
      setBody(body)
    }.body()
  }

  /** Forwards a message to new recipients. */
  suspend fun forward(messageId: String, block: ForwardMessageBuilder.() -> Unit): SendMessageResponse {
    require(messageId.isNotEmpty()) { "Message ID must not be empty." }
    val body = ForwardMessageBuilder().apply(block).build()
    return client.post("$basePath/${messageId.encodeURLPathPart()}/forward") {
      setBody(body)
    }.body()
  }

  /** Retrieves a message attachment's binary data. */
  suspend fun getAttachment(messageId: String, attachmentId: String): AttachmentData {
    require(messageId.isNotEmpty()) { "Message ID must not be empty." }
    require(attachmentId.isNotEmpty()) { "Attachment ID must not be empty." }
    val response =
      client.get("$basePath/${messageId.encodeURLPathPart()}/attachments/${attachmentId.encodeURLPathPart()}")
    return AttachmentData(
      data = response.body<ByteArray>(),
      contentType = response.headers["Content-Type"] ?: "application/octet-stream",
    )
  }

  /** Retrieves the raw RFC 2822 content of a message. */
  suspend fun getRaw(messageId: String): RawMessageResponse {
    require(messageId.isNotEmpty()) { "Message ID must not be empty." }
    return client.get("$basePath/${messageId.encodeURLPathPart()}/raw").body()
  }
}
