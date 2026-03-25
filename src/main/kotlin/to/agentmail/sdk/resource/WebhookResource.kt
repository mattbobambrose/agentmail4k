package to.agentmail.sdk.resource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.encodeURLPathPart
import to.agentmail.sdk.builder.CreateWebhookBuilder
import to.agentmail.sdk.builder.ListWebhooksBuilder
import to.agentmail.sdk.builder.UpdateWebhookBuilder
import to.agentmail.sdk.model.Webhook
import to.agentmail.sdk.model.WebhookList

class WebhookResource internal constructor(
  private val client: HttpClient,
  private val basePath: String,
) {

  suspend fun list(block: ListWebhooksBuilder.() -> Unit = {}): WebhookList {
    val params = ListWebhooksBuilder().apply(block).toQueryParams()
    return client.get(basePath) {
      params.forEach { (k, v) -> parameter(k, v) }
    }.body()
  }

  suspend fun create(block: CreateWebhookBuilder.() -> Unit): Webhook {
    val body = CreateWebhookBuilder().apply(block).build()
    return client.post(basePath) {
      setBody(body)
    }.body()
  }

  suspend fun get(webhookId: String): Webhook {
    require(webhookId.isNotEmpty()) { "Webhook ID must not be empty." }
    return client.get("$basePath/${webhookId.encodeURLPathPart()}").body()
  }

  suspend fun update(webhookId: String, block: UpdateWebhookBuilder.() -> Unit): Webhook {
    require(webhookId.isNotEmpty()) { "Webhook ID must not be empty." }
    val body = UpdateWebhookBuilder().apply(block).build()
    return client.patch("$basePath/${webhookId.encodeURLPathPart()}") {
      setBody(body)
    }.body()
  }

  suspend fun delete(webhookId: String) {
    require(webhookId.isNotEmpty()) { "Webhook ID must not be empty." }
    client.delete("$basePath/${webhookId.encodeURLPathPart()}")
  }
}
