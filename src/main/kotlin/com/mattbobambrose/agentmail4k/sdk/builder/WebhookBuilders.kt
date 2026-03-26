package com.mattbobambrose.agentmail4k.sdk.builder

import kotlinx.serialization.Serializable
import com.mattbobambrose.agentmail4k.sdk.AgentMailDsl
import com.mattbobambrose.agentmail4k.sdk.model.WebhookEvent

@AgentMailDsl
class CreateWebhookBuilder {
  var url: String? = null
  var events: List<String> = emptyList()

  fun events(vararg events: WebhookEvent) {
    this.events = events.map { it.value }
  }

  internal fun build(): CreateWebhookRequest {
    requireNotNull(url) { "Webhook URL is required" }
    return CreateWebhookRequest(url = url!!, events = events)
  }
}

@AgentMailDsl
class UpdateWebhookBuilder {
  var url: String? = null
  var events: List<String>? = null

  fun events(vararg events: WebhookEvent) {
    this.events = events.map { it.value }
  }

  internal fun build() = UpdateWebhookRequest(url = url, events = events)
}

@AgentMailDsl
class ListWebhooksBuilder {
  var limit: Int? = null
  var pageToken: String? = null
  var ascending: Boolean? = null

  internal fun toQueryParams(): Map<String, String> = buildMap {
    limit?.let { put("limit", it.toString()) }
    pageToken?.let { put("page_token", it) }
    ascending?.let { put("ascending", it.toString()) }
  }
}

@Serializable
internal data class CreateWebhookRequest(
  val url: String,
  val events: List<String>,
)

@Serializable
internal data class UpdateWebhookRequest(
  val url: String? = null,
  val events: List<String>? = null,
)
