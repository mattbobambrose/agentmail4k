package com.agentmail4k.sdk.builder

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.agentmail4k.sdk.AgentMailDsl

/** Builder for composing an outgoing email with sender, recipients, subject, and body. */
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

/** DSL builder for sending a message with recipients, subject, and body content. */
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

/** DSL builder for composing a reply with text and/or HTML body. */
@AgentMailDsl
class ReplyBuilder {
  var text: String? = null
  var html: String? = null

  internal fun build() = ReplyRequest(text = text, html = html)
}

/** DSL builder for composing a reply-all with text and/or HTML body. */
@AgentMailDsl
class ReplyAllBuilder {
  var text: String? = null
  var html: String? = null

  internal fun build() = ReplyAllRequest(text = text, html = html)
}

/** DSL builder for forwarding a message to new recipients. */
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

/** DSL builder for configuring message list pagination, filtering, and inclusion of spam/blocked/trash. */
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

/** DSL builder for updating message labels. Supports replacing all labels or incrementally adding/removing. */
@AgentMailDsl
class UpdateMessageBuilder {
  var labels: List<String>? = null
  private var _addLabels: MutableList<String>? = null
  private var _removeLabels: MutableList<String>? = null

  @Deprecated("addLabels() requires at least one label.", level = DeprecationLevel.ERROR)
  fun addLabels(): Nothing = throw UnsupportedOperationException()

  /** Adds the given labels to the message's existing labels. */
  fun addLabels(vararg labels: String) {
    _addLabels = (_addLabels ?: mutableListOf()).apply { addAll(labels) }
  }

  @Deprecated("removeLabels() requires at least one label.", level = DeprecationLevel.ERROR)
  fun removeLabels(): Nothing = throw UnsupportedOperationException()

  /** Removes the given labels from the message's existing labels. */
  fun removeLabels(vararg labels: String) {
    _removeLabels = (_removeLabels ?: mutableListOf()).apply { addAll(labels) }
  }

  internal fun build() = UpdateMessageRequest(
    labels = labels,
    addLabels = _addLabels?.toList(),
    removeLabels = _removeLabels?.toList(),
  )
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
  @SerialName("add_labels") val addLabels: List<String>? = null,
  @SerialName("remove_labels") val removeLabels: List<String>? = null,
)
