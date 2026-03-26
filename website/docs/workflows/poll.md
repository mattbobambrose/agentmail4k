# Polling

The `poll` extension function is a simplified wrapper around [Monitor](monitor.md) for the common case of watching an inbox for new messages.

## Usage

```kotlin
import com.mattbobambrose.agentmail4k.sdk.workflow.poll
import kotlin.time.Duration.Companion.seconds

val job = client.poll("inbox_abc", interval = 10.seconds) { message ->
    println("New message from ${message.from}: ${message.subject}")
}

// Stop polling
job.cancel()
```

## Parameters

| Parameter | Type | Default | Description |
|---|---|---|---|
| `inboxId` | `String` | *(required)* | Inbox to watch |
| `interval` | `Duration` | `10.seconds` | Time between polls |
| `scope` | `CoroutineScope` | `Dispatchers.Default` | Coroutine scope |
| `handler` | `suspend (Message) -> Unit` | *(required)* | Callback for each message |

## When to Use Poll vs Monitor

- **`poll`** — simple callback, no error handling or spam/blocked filtering needed
- **`monitor`** — need `onError`, `includeSpam`, `includeBlocked`, or custom poll intervals per-callback
