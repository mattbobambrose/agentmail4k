# Webhooks

Webhooks notify your application when events occur in AgentMail.

## Create a Webhook

```kotlin
--8<-- "Webhooks.kt:create-webhook"
```

### Available Events

| Event | Description |
|-------|-------------|
| `MESSAGE_RECEIVED` | A new message was received |
| `MESSAGE_SENT` | A message was sent |
| `MESSAGE_DELIVERED` | A message was delivered |
| `MESSAGE_BOUNCED` | A message bounced |
| `MESSAGE_COMPLAINED` | A spam complaint was received |
| `MESSAGE_REJECTED` | A message was rejected |
| `DOMAIN_VERIFIED` | A domain was verified |

## List Webhooks

```kotlin
--8<-- "Webhooks.kt:list-webhooks"
```

## Get a Webhook

```kotlin
--8<-- "Webhooks.kt:get-webhook"
```

## Update a Webhook

```kotlin
--8<-- "Webhooks.kt:update-webhook"
```

## Delete a Webhook

```kotlin
--8<-- "Webhooks.kt:delete-webhook"
```

## Webhook Handler

The SDK includes a webhook handler that verifies signatures and routes events to handlers.

### Basic Handler

```kotlin
--8<-- "Webhooks.kt:webhook-handler"
```

### Custom Event Handlers

Use the generic `on()` method for any event type:

```kotlin
--8<-- "Webhooks.kt:webhook-handler-custom"
```

### Signature Verification

Webhooks are signed using HMAC-SHA256 (Svix format). The handler verifies signatures automatically when `signingSecret` is set.

You can also verify signatures without dispatching:

```kotlin
--8<-- "Webhooks.kt:webhook-verify"
```
