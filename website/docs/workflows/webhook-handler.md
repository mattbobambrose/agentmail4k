# Webhook Handler

The `webhookHandler` function creates a handler that verifies and dispatches incoming webhook payloads.

## Basic Usage

```kotlin
import com.mattbobambrose.agentmail4k.sdk.workflow.webhookHandler
import com.mattbobambrose.agentmail4k.sdk.model.WebhookEvent

val handler = webhookHandler {
    signingSecret = "whsec_your_secret_here"

    onMessageReceived { payload ->
        println("New message: $payload")
    }

    onMessageBounced { payload ->
        println("Message bounced: $payload")
    }

    onDomainVerified { payload ->
        println("Domain verified: $payload")
    }
}
```

## Processing a Request

```kotlin
// In your HTTP handler (Ktor, Spring, etc.)
val headers = mapOf(
    "svix-id" to request.header("svix-id"),
    "svix-timestamp" to request.header("svix-timestamp"),
    "svix-signature" to request.header("svix-signature"),
)
val body = request.bodyAsText()

val success = handler.handle(headers, body)
if (!success) {
    // Signature verification failed or unknown event
    respond(HttpStatusCode.Unauthorized)
}
```

## Signature Verification

Webhooks are signed using HMAC-SHA256 via the [Svix](https://www.svix.com/) protocol. The handler verifies signatures automatically when `signingSecret` is set.

The verification process:

1. Extracts `svix-id`, `svix-timestamp`, and `svix-signature` from headers
2. Computes `HMAC-SHA256("$svixId.$svixTimestamp.$body", secret)`
3. Compares against the signature(s) in the header

If `signingSecret` is `null`, verification is skipped.

## Event Handlers

Register handlers using convenience methods or the generic `on` method:

```kotlin
val handler = webhookHandler {
    // Convenience methods
    onMessageReceived { payload -> /* ... */ }
    onMessageSent { payload -> /* ... */ }
    onMessageDelivered { payload -> /* ... */ }
    onMessageBounced { payload -> /* ... */ }
    onMessageComplained { payload -> /* ... */ }
    onMessageRejected { payload -> /* ... */ }
    onDomainVerified { payload -> /* ... */ }

    // Generic method
    on(WebhookEvent.MESSAGE_RECEIVED) { payload -> /* ... */ }
}
```

Each handler receives a `JsonObject` containing the full webhook payload. You can extract fields using kotlinx.serialization:

```kotlin
onMessageReceived { payload ->
    val messageId = payload["data"]?.jsonObject?.get("message_id")?.jsonPrimitive?.content
    println("Received message: $messageId")
}
```

## Verify Only

You can verify a signature without dispatching:

```kotlin
val isValid = handler.verify(headers, body)
```
