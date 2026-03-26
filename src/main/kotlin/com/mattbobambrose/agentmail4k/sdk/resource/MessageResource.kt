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

class MessageResource internal constructor(
  private val client: HttpClient,
  private val basePath: String,
) {
  suspend fun list(block: ListMessagesBuilder.() -> Unit = {}): MessageList {
    val params = ListMessagesBuilder().apply(block).toQueryParams()
    return client.get(basePath) {
      params.forEach { (k, v) -> parameter(k, v) }
    }.body()
  }

  suspend fun get(messageId: String): Message {
    require(messageId.isNotEmpty()) { "Message ID must not be empty." }
    return client.get("$basePath/${messageId.encodeURLPathPart()}").body()
  }

  suspend fun update(messageId: String, block: UpdateMessageBuilder.() -> Unit): Message {
    require(messageId.isNotEmpty()) { "Message ID must not be empty." }
    val body = UpdateMessageBuilder().apply(block).build()
    return client.patch("$basePath/${messageId.encodeURLPathPart()}") {
      setBody(body)
    }.body()
  }

  suspend fun send(block: SendMessageBuilder.() -> Unit): SendMessageResponse {
    val body = SendMessageBuilder().apply(block).build()
    return client.post("$basePath/send") {
      setBody(body)
    }.body()
  }

  suspend fun reply(messageId: String, block: ReplyBuilder.() -> Unit): SendMessageResponse {
    require(messageId.isNotEmpty()) { "Message ID must not be empty." }
    val body = ReplyBuilder().apply(block).build()
    return client.post("$basePath/${messageId.encodeURLPathPart()}/reply") {
      setBody(body)
    }.body()
  }

  suspend fun replyAll(messageId: String, block: ReplyAllBuilder.() -> Unit): SendMessageResponse {
    require(messageId.isNotEmpty()) { "Message ID must not be empty." }
    val body = ReplyAllBuilder().apply(block).build()
    return client.post("$basePath/${messageId.encodeURLPathPart()}/reply-all") {
      setBody(body)
    }.body()
  }

  suspend fun forward(messageId: String, block: ForwardMessageBuilder.() -> Unit): SendMessageResponse {
    require(messageId.isNotEmpty()) { "Message ID must not be empty." }
    val body = ForwardMessageBuilder().apply(block).build()
    return client.post("$basePath/${messageId.encodeURLPathPart()}/forward") {
      setBody(body)
    }.body()
  }

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

  suspend fun getRaw(messageId: String): RawMessageResponse {
    require(messageId.isNotEmpty()) { "Message ID must not be empty." }
    return client.get("$basePath/${messageId.encodeURLPathPart()}/raw").body()
  }
}
