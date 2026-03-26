package com.mattbobambrose.agentmail4k.sdk.builder

import kotlinx.serialization.Serializable
import com.mattbobambrose.agentmail4k.sdk.AgentMailDsl

@AgentMailDsl
class CreateDraftBuilder {
  var to: List<String> = emptyList()
  var cc: List<String>? = null
  var bcc: List<String>? = null
  var subject: String? = null
  var text: String? = null
  var html: String? = null

  internal fun build() = CreateDraftRequest(
    to = to,
    cc = cc,
    bcc = bcc,
    subject = subject,
    text = text,
    html = html,
  )
}

@AgentMailDsl
class UpdateDraftBuilder {
  var to: List<String>? = null
  var cc: List<String>? = null
  var bcc: List<String>? = null
  var subject: String? = null
  var text: String? = null
  var html: String? = null

  internal fun build() = UpdateDraftRequest(
    to = to,
    cc = cc,
    bcc = bcc,
    subject = subject,
    text = text,
    html = html,
  )
}

@AgentMailDsl
class ListDraftsBuilder {
  var limit: Int? = null
  var pageToken: String? = null
  var labels: List<String>? = null
  var before: String? = null
  var after: String? = null
  var ascending: Boolean? = null

  internal fun toQueryParams(): Map<String, String> = buildMap {
    limit?.let { put("limit", it.toString()) }
    pageToken?.let { put("page_token", it) }
    labels?.let { put("labels", it.joinToString(",")) }
    before?.let { put("before", it) }
    after?.let { put("after", it) }
    ascending?.let { put("ascending", it.toString()) }
  }
}

@AgentMailDsl
class SendDraftBuilder {
  var labels: List<String>? = null

  internal fun build() = UpdateMessageRequest(labels = labels)
}

@Serializable
internal data class CreateDraftRequest(
  val to: List<String>,
  val cc: List<String>? = null,
  val bcc: List<String>? = null,
  val subject: String? = null,
  val text: String? = null,
  val html: String? = null,
)

@Serializable
internal data class UpdateDraftRequest(
  val to: List<String>? = null,
  val cc: List<String>? = null,
  val bcc: List<String>? = null,
  val subject: String? = null,
  val text: String? = null,
  val html: String? = null,
)
