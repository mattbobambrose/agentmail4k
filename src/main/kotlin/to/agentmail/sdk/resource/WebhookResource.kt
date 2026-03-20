package to.agentmail.sdk.resource

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import to.agentmail.sdk.builder.CreateWebhookBuilder
import to.agentmail.sdk.builder.ListWebhooksBuilder
import to.agentmail.sdk.builder.UpdateWebhookBuilder
import to.agentmail.sdk.model.Webhook
import to.agentmail.sdk.model.WebhookList

class WebhookResource internal constructor(
    private val client: HttpClient,
) {
    private val basePath = "v0/webhooks"

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
        return client.get("$basePath/$webhookId").body()
    }

    suspend fun update(webhookId: String, block: UpdateWebhookBuilder.() -> Unit): Webhook {
        val body = UpdateWebhookBuilder().apply(block).build()
        return client.patch("$basePath/$webhookId") {
            setBody(body)
        }.body()
    }

    suspend fun delete(webhookId: String) {
        client.delete("$basePath/$webhookId")
    }
}
