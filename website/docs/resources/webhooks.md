# Webhooks

Webhooks notify your server when events occur (messages received, sent, bounced, etc.).

## List Webhooks

```kotlin
val result = client.webhooks.list {
    limit = 25
}

for (webhook in result.webhooks) {
    println("${webhook.url} — events: ${webhook.events}")
}
```

## Create Webhook

```kotlin
val webhook = client.webhooks.create {
    url = "https://example.com/webhooks/agentmail"
    events = listOf("message.received", "message.bounced")
}

println(webhook.webhookId)
```

## Get Webhook

```kotlin
val webhook = client.webhooks.get("webhook_123")
```

## Update Webhook

```kotlin
val updated = client.webhooks.update("webhook_123") {
    url = "https://example.com/webhooks/v2"
    events = listOf("message.received")
}
```

## Delete Webhook

```kotlin
client.webhooks.delete("webhook_123")
```

## Available Events

| Event | Description |
|---|---|
| `message.received` | A new message was received |
| `message.sent` | A message was sent |
| `message.delivered` | A message was delivered |
| `message.bounced` | A message bounced |
| `message.complained` | A spam complaint was filed |
| `message.rejected` | A message was rejected |
| `domain.verified` | A domain was verified |

These are available as the `WebhookEvent` enum:

```kotlin
import to.agentmail.sdk.model.WebhookEvent

WebhookEvent.MESSAGE_RECEIVED  // "message.received"
WebhookEvent.MESSAGE_BOUNCED   // "message.bounced"
// etc.
```

## Handling Webhooks

See [Webhook Handler](../workflows/webhook-handler.md) for processing incoming webhook payloads with signature verification.
