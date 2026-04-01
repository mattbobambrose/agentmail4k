package com.agentmail4k.sdk.builder

import kotlinx.serialization.Serializable
import com.agentmail4k.sdk.AgentMailDsl
import com.agentmail4k.sdk.model.WebhookEvent

/** DSL builder for creating a webhook with a URL and event subscriptions. */
@AgentMailDsl
class CreateWebhookBuilder {
  var url: String? = null
  var events: List<String> = emptyList()

  /** Sets the webhook event subscriptions using type-safe [WebhookEvent] values. */
  fun events(vararg events: WebhookEvent) {
    this.events = events.map { it.value }
  }

  internal fun build(): CreateWebhookRequest {
    requireNotNull(url) { "Webhook URL is required" }
    return CreateWebhookRequest(url = url!!, events = events)
  }
}

/** DSL builder for updating a webhook's URL and/or event subscriptions. */
@AgentMailDsl
class UpdateWebhookBuilder {
  var url: String? = null
  var events: List<String>? = null

  /** Sets the webhook event subscriptions using type-safe [WebhookEvent] values. */
  fun events(vararg events: WebhookEvent) {
    this.events = events.map { it.value }
  }

  internal fun build() = UpdateWebhookRequest(url = url, events = events)
}

/** DSL builder for configuring webhook list pagination. */
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
