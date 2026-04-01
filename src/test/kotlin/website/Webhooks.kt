@file:Suppress("unused")

package website

import com.agentmail4k.dsl.createWebhook
import com.agentmail4k.dsl.deleteWebhook
import com.agentmail4k.dsl.getWebhook
import com.agentmail4k.dsl.listWebhooks
import com.agentmail4k.dsl.updateWebhook
import com.agentmail4k.dsl.webhookHandler
import com.agentmail4k.sdk.AgentMailClient
import com.agentmail4k.sdk.model.WebhookEvent

// --8<-- [start:create-webhook]
suspend fun createWebhookExample() {
    val client = AgentMailClient()
    val webhook = client.createWebhook {
        url = "https://example.com/webhook"
        events(WebhookEvent.MESSAGE_RECEIVED, WebhookEvent.MESSAGE_SENT)
    }
    println("Webhook ID: ${webhook.webhookId}")
    client.close()
}
// --8<-- [end:create-webhook]

// --8<-- [start:list-webhooks]
suspend fun listWebhooksExample() {
    val client = AgentMailClient()
    val result = client.listWebhooks {
        limit = 10
    }
    for (webhook in result.webhooks) {
        println("${webhook.webhookId}: ${webhook.url}")
    }
    client.close()
}
// --8<-- [end:list-webhooks]

// --8<-- [start:get-webhook]
suspend fun getWebhookExample() {
    val client = AgentMailClient()
    val webhook = client.getWebhook("webhook-id")
    println("URL: ${webhook.url}")
    println("Events: ${webhook.events}")
    client.close()
}
// --8<-- [end:get-webhook]

// --8<-- [start:update-webhook]
suspend fun updateWebhookExample() {
    val client = AgentMailClient()
    val updated = client.updateWebhook("webhook-id") {
        url = "https://example.com/new-webhook"
        events(WebhookEvent.MESSAGE_RECEIVED)
    }
    println("Updated URL: ${updated.url}")
    client.close()
}
// --8<-- [end:update-webhook]

// --8<-- [start:delete-webhook]
suspend fun deleteWebhookExample() {
    val client = AgentMailClient()
    client.deleteWebhook("webhook-id")
    println("Webhook deleted")
    client.close()
}
// --8<-- [end:delete-webhook]

// --8<-- [start:webhook-handler]
fun webhookHandlerExample() {
    val handler = webhookHandler {
        signingSecret = "whsec_your-signing-secret"

        onMessageReceived { payload ->
            println("Message received: $payload")
        }

        onMessageSent { payload ->
            println("Message sent: $payload")
        }

        onMessageBounced { payload ->
            println("Message bounced: $payload")
        }
    }

    // In your HTTP handler:
    // val headers = mapOf("svix-id" to ..., "svix-timestamp" to ..., "svix-signature" to ...)
    // val success = handler.handle(headers, requestBody)
}
// --8<-- [end:webhook-handler]

// --8<-- [start:webhook-handler-custom]
fun webhookHandlerCustomExample() {
    val handler = webhookHandler {
        signingSecret = "whsec_your-signing-secret"

        // Use the generic on() for any event type
        on(WebhookEvent.DOMAIN_VERIFIED) { payload ->
            println("Domain verified: $payload")
        }

        on(WebhookEvent.MESSAGE_COMPLAINED) { payload ->
            println("Complaint received: $payload")
        }
    }

    // Use handler.handle(headers, body) to verify and dispatch
    // See the basic handler example above for the full pattern
}
// --8<-- [end:webhook-handler-custom]

// --8<-- [start:webhook-verify]
fun webhookVerifyExample() {
    val handler = webhookHandler {
        signingSecret = "whsec_your-signing-secret"
    }

    // Verify signature without dispatching to handlers
    val headers = mapOf(
        "svix-id" to "msg_abc123",
        "svix-timestamp" to "1234567890",
        "svix-signature" to "v1,signature-here"
    )
    val isValid = handler.verify(headers, """{"type":"message.received"}""")
    println("Valid: $isValid")
}
// --8<-- [end:webhook-verify]
