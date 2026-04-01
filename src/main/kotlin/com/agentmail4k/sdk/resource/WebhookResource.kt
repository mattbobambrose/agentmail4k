package com.agentmail4k.sdk.resource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.encodeURLPathPart
import com.agentmail4k.sdk.builder.CreateWebhookBuilder
import com.agentmail4k.sdk.builder.ListWebhooksBuilder
import com.agentmail4k.sdk.builder.UpdateWebhookBuilder
import com.agentmail4k.sdk.model.Webhook
import com.agentmail4k.sdk.model.WebhookList

/** Provides operations for managing webhooks: list, create, get, update, and delete. */
class WebhookResource internal constructor(
  private val client: HttpClient,
  private val basePath: String,
) {

  /** Lists webhooks with optional pagination. */
  suspend fun list(block: ListWebhooksBuilder.() -> Unit = {}): WebhookList {
    val params = ListWebhooksBuilder().apply(block).toQueryParams()
    return client.get(basePath) {
      params.forEach { (k, v) -> parameter(k, v) }
    }.body()
  }

  /** Creates a new webhook. */
  suspend fun create(block: CreateWebhookBuilder.() -> Unit): Webhook {
    val body = CreateWebhookBuilder().apply(block).build()
    return client.post(basePath) {
      setBody(body)
    }.body()
  }

  /** Retrieves a webhook by ID. */
  suspend fun get(webhookId: String): Webhook {
    require(webhookId.isNotEmpty()) { "Webhook ID must not be empty." }
    return client.get("$basePath/${webhookId.encodeURLPathPart()}").body()
  }

  /** Updates a webhook by ID. */
  suspend fun update(webhookId: String, block: UpdateWebhookBuilder.() -> Unit): Webhook {
    require(webhookId.isNotEmpty()) { "Webhook ID must not be empty." }
    val body = UpdateWebhookBuilder().apply(block).build()
    return client.patch("$basePath/${webhookId.encodeURLPathPart()}") {
      setBody(body)
    }.body()
  }

  /** Deletes a webhook by ID. */
  suspend fun delete(webhookId: String) {
    require(webhookId.isNotEmpty()) { "Webhook ID must not be empty." }
    client.delete("$basePath/${webhookId.encodeURLPathPart()}")
  }
}
