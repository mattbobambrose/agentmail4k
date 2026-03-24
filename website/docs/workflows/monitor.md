# Monitor

The `monitor` extension function polls an inbox for new messages and invokes a callback for each one. It runs as a coroutine `Job` that can be cancelled.

## Basic Usage

```kotlin
import to.agentmail.sdk.workflow.monitor
import kotlin.time.Duration.Companion.seconds

val job = client.monitor("inbox_abc") {
    pollInterval = 5.seconds

    onMessage { message ->
        println("From: ${message.from}")
        println("Subject: ${message.subject}")
        println("Body: ${message.text}")
    }

    onError { error ->
        System.err.println("Monitor error: ${error.message}")
    }
}

// Later, stop monitoring
job.cancel()
```

## Configuration

| Parameter | Type | Default | Description |
|---|---|---|---|
| `pollInterval` | `Duration` | `5.seconds` | Time between polls |
| `includeSpam` | `Boolean` | `false` | Include spam messages |
| `includeBlocked` | `Boolean` | `false` | Include blocked messages |

## Callbacks

| Callback | Description |
|---|---|
| `onMessage { message -> }` | Called for each new message |
| `onError { error -> }` | Called when a poll cycle fails |

## Custom Coroutine Scope

By default, the monitor runs on `Dispatchers.Default` with a `SupervisorJob`. You can provide your own scope:

```kotlin
val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

val job = client.monitor("inbox_abc", scope) {
    onMessage { println(it.subject) }
}
```

## How It Works

The monitor polls the inbox's message list endpoint, tracking the timestamp of the most recent message. On each cycle it fetches up to 50 new messages and delivers them to the callback in chronological order. `CancellationException` is re-thrown to support cooperative cancellation.
