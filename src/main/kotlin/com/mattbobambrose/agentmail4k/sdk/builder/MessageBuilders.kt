package com.mattbobambrose.agentmail4k.sdk.builder

import kotlinx.serialization.Serializable
import com.mattbobambrose.agentmail4k.sdk.AgentMailDsl

class SendMessage {
  var from: String = ""
  var to: List<String> = emptyList()
  var cc: List<String>? = null
  var bcc: List<String>? = null
  var subject: String? = null
  var text: String? = null
  var html: String? = null

  internal fun build(): SendMessageRequest {
    check(to.isNotEmpty()) { "At least one recipient (to) is required" }
    return SendMessageRequest(
      to = to,
      cc = cc,
      bcc = bcc,
      subject = subject,
      text = text,
      html = html,
    )
  }
}

@AgentMailDsl
class SendMessageBuilder {
  var to: List<String> = emptyList()
  var cc: List<String>? = null
  var bcc: List<String>? = null
  var subject: String? = null
  var text: String? = null
  var html: String? = null

  internal fun build(): SendMessageRequest {
    check(to.isNotEmpty()) { "At least one recipient (to) is required" }
    return SendMessageRequest(
      to = to,
      cc = cc,
      bcc = bcc,
      subject = subject,
      text = text,
      html = html,
    )
  }
}

@AgentMailDsl
class ReplyBuilder {
  var text: String? = null
  var html: String? = null

  internal fun build() = ReplyRequest(text = text, html = html)
}

@AgentMailDsl
class ReplyAllBuilder {
  var text: String? = null
  var html: String? = null

  internal fun build() = ReplyAllRequest(text = text, html = html)
}

@AgentMailDsl
class ForwardMessageBuilder {
  var to: List<String> = emptyList()
  var cc: List<String>? = null
  var bcc: List<String>? = null
  var subject: String? = null
  var text: String? = null
  var html: String? = null

  internal fun build(): SendMessageRequest {
    check(to.isNotEmpty()) { "At least one recipient (to) is required for forwarding" }
    return SendMessageRequest(
      to = to,
      cc = cc,
      bcc = bcc,
      subject = subject,
      text = text,
      html = html,
    )
  }
}

@AgentMailDsl
class ListMessagesBuilder {
  var limit: Int? = null
  var pageToken: String? = null
  var labels: List<String>? = null
  var before: String? = null
  var after: String? = null
  var ascending: Boolean? = null
  var includeSpam: Boolean? = null
  var includeBlocked: Boolean? = null
  var includeTrash: Boolean? = null

  internal fun toQueryParams(): Map<String, String> = buildMap {
    limit?.let { put("limit", it.toString()) }
    pageToken?.let { put("page_token", it) }
    labels?.let { put("labels", it.joinToString(",")) }
    before?.let { put("before", it) }
    after?.let { put("after", it) }
    ascending?.let { put("ascending", it.toString()) }
    includeSpam?.let { put("include_spam", it.toString()) }
    includeBlocked?.let { put("include_blocked", it.toString()) }
    includeTrash?.let { put("include_trash", it.toString()) }
  }
}

@AgentMailDsl
class UpdateMessageBuilder {
  var labels: List<String>? = null

  internal fun build() = UpdateMessageRequest(labels = labels)
}

@Serializable
internal data class SendMessageRequest(
  val to: List<String>,
  val cc: List<String>? = null,
  val bcc: List<String>? = null,
  val subject: String? = null,
  val text: String? = null,
  val html: String? = null,
)

@Serializable
internal data class ReplyRequest(
  val text: String? = null,
  val html: String? = null,
)

@Serializable
internal data class ReplyAllRequest(
  val text: String? = null,
  val html: String? = null,
)

@Serializable
internal data class UpdateMessageRequest(
  val labels: List<String>? = null,
)
